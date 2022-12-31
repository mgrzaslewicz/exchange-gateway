package automate.profit.autocoin.exchange.order

import automate.profit.autocoin.api.exchange.order.Order
import automate.profit.autocoin.exchange.currency.defaultXchangeCurrencyPairTransformer
import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.order.OrderSide
import automate.profit.autocoin.spi.exchange.order.OrderStatus
import org.knowm.xchange.dto.trade.LimitOrder
import java.util.function.Function
import automate.profit.autocoin.spi.exchange.order.Order as SpiOrder
import org.knowm.xchange.dto.Order as XchangeOrder

val defaultXchangeOrderStatusTransformer: Function<XchangeOrder.OrderStatus, OrderStatus> = Function {
    when (it) {
        XchangeOrder.OrderStatus.NEW -> OrderStatus.NEW
        XchangeOrder.OrderStatus.PENDING_NEW -> OrderStatus.NEW
        XchangeOrder.OrderStatus.FILLED -> OrderStatus.FILLED
        XchangeOrder.OrderStatus.PARTIALLY_CANCELED -> OrderStatus.PARTIALLY_CANCELED
        XchangeOrder.OrderStatus.PARTIALLY_FILLED -> OrderStatus.PARTIALLY_FILLED
        XchangeOrder.OrderStatus.CANCELED -> OrderStatus.CANCELED
        else -> throw IllegalStateException("Status $it not handled")
    }
}

val defaultXchangeTypeToOrderSide: Function<XchangeOrder.OrderType, OrderSide> = Function {
    when (it) {
        XchangeOrder.OrderType.ASK -> OrderSide.ASK_SELL
        XchangeOrder.OrderType.BID -> OrderSide.BID_BUY
        else -> throw IllegalStateException("Type $it not handled")
    }
}

val defaultXchangeLimitOrderToOrderTransformer = object : XchangeLimitOrderToOrderTransformer {
    override fun invoke(
        exchangeName: ExchangeName,
        xchangeLimitOrder: LimitOrder,
        receivedAtMillis: Long,
    ): SpiOrder {
        return Order(
            exchangeName = exchangeName,
            exchangeOrderId = xchangeLimitOrder.id,
            side = defaultXchangeTypeToOrderSide.apply(xchangeLimitOrder.type),
            orderedAmount = xchangeLimitOrder.originalAmount,
            filledAmount = xchangeLimitOrder.cumulativeAmount,
            price = xchangeLimitOrder.averagePrice ?: xchangeLimitOrder.limitPrice,
            currencyPair = defaultXchangeCurrencyPairTransformer.apply(xchangeLimitOrder.currencyPair),
            status = defaultXchangeOrderStatusTransformer.apply(xchangeLimitOrder.status),
            receivedAtMillis = receivedAtMillis,
            exchangeTimestampMillis = xchangeLimitOrder.timestamp?.time,
        )
    }

}
