package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.order.OrderSide
import automate.profit.autocoin.spi.exchange.orderbook.OrderBook
import java.util.function.Function
import org.knowm.xchange.currency.CurrencyPair as XchangeCurrencyPair
import org.knowm.xchange.dto.Order as XchangeOrder
import org.knowm.xchange.dto.marketdata.OrderBook as XchangeOrderBook

interface XchangeOrderBookTransformer {
    operator fun invoke(
        xchangeOrderBook: XchangeOrderBook,
        receivedAtMillis: Long,
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
        xchangeLimitOrderToOrderInOrderBookTransformer: XchangeLimitOrderToOrderInOrderBookTransformer,
        xchangeOrderTypeTransformer: Function<XchangeOrder.OrderType, OrderSide>,
        xchangeCurrencyPairTransformer: Function<XchangeCurrencyPair, CurrencyPair>,
    ): OrderBook
}
