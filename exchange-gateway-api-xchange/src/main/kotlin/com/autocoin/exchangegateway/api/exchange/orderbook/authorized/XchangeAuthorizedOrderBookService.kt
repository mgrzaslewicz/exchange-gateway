package com.autocoin.exchangegateway.api.exchange.orderbook.authorized

import com.autocoin.exchangegateway.api.exchange.currency.defaultCurrencyPairToXchange
import com.autocoin.exchangegateway.api.exchange.currency.defaultXchangeCurrencyPairTransformer
import com.autocoin.exchangegateway.api.exchange.order.defaultXchangeTypeToOrderSide
import com.autocoin.exchangegateway.api.exchange.orderbook.OrderBook
import com.autocoin.exchangegateway.api.exchange.orderbook.XchangeLimitOrderToOrderInOrderBookTransformer
import com.autocoin.exchangegateway.api.exchange.orderbook.XchangeOrderBookTransformer
import com.autocoin.exchangegateway.api.exchange.orderbook.defaultXchangeLimitOrderToOrderInOrderBookTransformer
import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide
import com.autocoin.exchangegateway.spi.exchange.orderbook.service.authorized.AuthorizedOrderBookService
import java.time.Clock
import java.util.function.Function
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderBook as SpiOrderBook
import org.knowm.xchange.currency.CurrencyPair as XchangeCurrencyPair
import org.knowm.xchange.dto.Order as XchangeOrder
import org.knowm.xchange.dto.marketdata.OrderBook as XchangeOrderBook
import org.knowm.xchange.service.marketdata.MarketDataService as XchangeMarketDataService


class XchangeAuthorizedOrderBookService<T>(
    override val exchange: Exchange,
    override val apiKey: ApiKeySupplier<T>,
    val delegate: XchangeMarketDataService,
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
                exchange: Exchange,
                currencyPair: CurrencyPair,
                xchangeLimitOrderToOrderInOrderBookTransformer: XchangeLimitOrderToOrderInOrderBookTransformer,
                xchangeOrderTypeTransformer: Function<XchangeOrder.OrderType, OrderSide>,
                xchangeCurrencyPairTransformer: Function<XchangeCurrencyPair, CurrencyPair>,
            ): SpiOrderBook {
                return OrderBook(
                    exchange = exchange,
                    currencyPair = currencyPair,
                    buyOrders = xchangeOrderBook.bids.map {
                        xchangeLimitOrderToOrderInOrderBookTransformer(
                            xchangeLimitOrder = it,
                            receivedAtMillis = receivedAtMillis,
                            exchange = exchange,
                            xchangeCurrencyPairTransformer = xchangeCurrencyPairTransformer,
                            xchangeOrderTypeTransformer = xchangeOrderTypeTransformer,
                        )
                    },
                    sellOrders = xchangeOrderBook.asks.map {
                        xchangeLimitOrderToOrderInOrderBookTransformer(
                            xchangeLimitOrder = it,
                            receivedAtMillis = receivedAtMillis,
                            exchange = exchange,
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
            exchange = exchange,
            currencyPair = currencyPair,
            xchangeLimitOrderToOrderInOrderBookTransformer = xchangeLimitOrderToOrderInOrderBookTransformer,
            xchangeOrderTypeTransformer = xchangeTypeToOrderSide,
            xchangeCurrencyPairTransformer = xchangeCurrencyPairTransformer,
        )
    }

}
