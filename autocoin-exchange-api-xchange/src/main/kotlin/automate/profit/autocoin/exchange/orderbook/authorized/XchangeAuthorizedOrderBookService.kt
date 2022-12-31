package automate.profit.autocoin.exchange.orderbook.authorized

import automate.profit.autocoin.api.exchange.orderbook.OrderBook
import automate.profit.autocoin.exchange.currency.defaultCurrencyPairToXchange
import automate.profit.autocoin.exchange.currency.defaultXchangeCurrencyPairTransformer
import automate.profit.autocoin.exchange.order.defaultXchangeTypeToOrderSide
import automate.profit.autocoin.exchange.orderbook.XchangeLimitOrderToOrderInOrderBookTransformer
import automate.profit.autocoin.exchange.orderbook.XchangeOrderBookTransformer
import automate.profit.autocoin.exchange.orderbook.defaultXchangeLimitOrderToOrderInOrderBookTransformer
import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.order.OrderSide
import automate.profit.autocoin.spi.exchange.orderbook.service.authorized.AuthorizedOrderBookService
import org.knowm.xchange.service.marketdata.MarketDataService
import java.time.Clock
import java.util.function.Function
import automate.profit.autocoin.spi.exchange.orderbook.OrderBook as SpiOrderBook
import org.knowm.xchange.currency.CurrencyPair as XchangeCurrencyPair
import org.knowm.xchange.dto.Order as XchangeOrder
import org.knowm.xchange.dto.marketdata.OrderBook as XchangeOrderBook


class XchangeAuthorizedOrderBookService<T>(
    override val exchangeName: ExchangeName,
    override val apiKey: ApiKeySupplier<T>,
    val delegate: MarketDataService,
    private val clock: Clock,
    private val xchangeCurrencyPairTransformer: Function<XchangeCurrencyPair, CurrencyPair> = defaultXchangeCurrencyPairTransformer,
    private val currencyPairToXchange: Function<CurrencyPair, XchangeCurrencyPair> = defaultCurrencyPairToXchange,
    private val xchangeOrderBookTransformer: XchangeOrderBookTransformer = defaultXchangeOrderBookTransformer,
    private val xchangeTypeToOrderSide: Function<XchangeOrder.OrderType, OrderSide> = defaultXchangeTypeToOrderSide,
    private val xchangeLimitOrderToOrderInOrderBookTransformer: XchangeLimitOrderToOrderInOrderBookTransformer = defaultXchangeLimitOrderToOrderInOrderBookTransformer,
) : AuthorizedOrderBookService<T> {
    companion object {

        val defaultXchangeOrderBookTransformer: XchangeOrderBookTransformer = object : XchangeOrderBookTransformer {
            override operator fun invoke(
                xchangeOrderBook: XchangeOrderBook,
                receivedAtMillis: Long,
                exchangeName: ExchangeName,
                currencyPair: CurrencyPair,
                xchangeLimitOrderToOrderInOrderBookTransformer: XchangeLimitOrderToOrderInOrderBookTransformer,
                xchangeOrderTypeTransformer: Function<XchangeOrder.OrderType, OrderSide>,
                xchangeCurrencyPairTransformer: Function<XchangeCurrencyPair, CurrencyPair>,
            ): SpiOrderBook {
                return OrderBook(
                    exchangeName = exchangeName,
                    currencyPair = currencyPair,
                    buyOrders = xchangeOrderBook.bids.map {
                        xchangeLimitOrderToOrderInOrderBookTransformer(
                            xchangeLimitOrder = it,
                            receivedAtMillis = receivedAtMillis,
                            exchangeName = exchangeName,
                            xchangeCurrencyPairTransformer = xchangeCurrencyPairTransformer,
                            xchangeOrderTypeTransformer = xchangeOrderTypeTransformer,
                        )
                    },
                    sellOrders = xchangeOrderBook.asks.map {
                        xchangeLimitOrderToOrderInOrderBookTransformer(
                            xchangeLimitOrder = it,
                            receivedAtMillis = receivedAtMillis,
                            exchangeName = exchangeName,
                            xchangeCurrencyPairTransformer = xchangeCurrencyPairTransformer,
                            xchangeOrderTypeTransformer = xchangeOrderTypeTransformer,
                        )
                    },
                    receivedAtMillis = receivedAtMillis,
                    exchangeTimestampMillis = xchangeOrderBook.timeStamp?.time,
                )
            }
        }
    }

    override fun getOrderBook(currencyPair: CurrencyPair): SpiOrderBook {
        val xchangeOrderBook = delegate.getOrderBook(currencyPairToXchange.apply(currencyPair))
        return xchangeOrderBookTransformer(
            xchangeOrderBook = xchangeOrderBook,
            receivedAtMillis = clock.millis(),
            exchangeName = exchangeName,
            currencyPair = currencyPair,
            xchangeLimitOrderToOrderInOrderBookTransformer = xchangeLimitOrderToOrderInOrderBookTransformer,
            xchangeOrderTypeTransformer = xchangeTypeToOrderSide,
            xchangeCurrencyPairTransformer = xchangeCurrencyPairTransformer,
        )
    }

}
