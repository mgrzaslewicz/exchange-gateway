package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.order.OrderSide
import automate.profit.autocoin.spi.exchange.orderbook.OrderInOrderBook
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.Order
import org.knowm.xchange.dto.trade.LimitOrder
import java.util.function.Function

val defaultXchangeLimitOrderToOrderInOrderBookTransformer: XchangeLimitOrderToOrderInOrderBookTransformer = object : XchangeLimitOrderToOrderInOrderBookTransformer {
    override operator fun invoke(
        xchangeLimitOrder: LimitOrder,
        exchangeName: ExchangeName,
        receivedAtMillis: Long,
        xchangeOrderTypeTransformer: Function<Order.OrderType, OrderSide>,
        xchangeCurrencyPairTransformer: Function<CurrencyPair, automate.profit.autocoin.spi.exchange.currency.CurrencyPair>,
    ): OrderInOrderBook {
        return automate.profit.autocoin.api.exchange.orderbook.OrderInOrderBook(
            exchangeName = exchangeName,
            side = xchangeOrderTypeTransformer.apply(xchangeLimitOrder.type),
            orderedAmount = xchangeLimitOrder.originalAmount,
            price = xchangeLimitOrder.averagePrice ?: xchangeLimitOrder.limitPrice,
            currencyPair = xchangeCurrencyPairTransformer.apply(xchangeLimitOrder.currencyPair),
            receivedAtMillis = receivedAtMillis,
            exchangeTimestampMillis = xchangeLimitOrder.timestamp?.time,
        )
    }
}
