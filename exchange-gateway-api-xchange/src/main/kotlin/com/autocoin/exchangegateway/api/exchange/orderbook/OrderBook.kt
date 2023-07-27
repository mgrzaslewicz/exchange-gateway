package com.autocoin.exchangegateway.api.exchange.orderbook

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.Order
import org.knowm.xchange.dto.trade.LimitOrder
import java.util.function.Function
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair as SpiCurrencyPair

val defaultXchangeLimitOrderToOrderInOrderBookTransformer: XchangeLimitOrderToOrderInOrderBookTransformer = object : XchangeLimitOrderToOrderInOrderBookTransformer {
    override operator fun invoke(
        xchangeLimitOrder: LimitOrder,
        exchange: Exchange,
        receivedAtMillis: Long,
        xchangeOrderTypeTransformer: Function<Order.OrderType, OrderSide>,
        xchangeCurrencyPairTransformer: Function<CurrencyPair, SpiCurrencyPair>,
    ): OrderInOrderBook {
        return OrderInOrderBook(
            exchange = exchange,
            side = xchangeOrderTypeTransformer.apply(xchangeLimitOrder.type),
            orderedAmount = xchangeLimitOrder.originalAmount,
            price = xchangeLimitOrder.averagePrice ?: xchangeLimitOrder.limitPrice,
            currencyPair = xchangeCurrencyPairTransformer.apply(xchangeLimitOrder.currencyPair),
            receivedAtMillis = receivedAtMillis,
            exchangeTimestampMillis = xchangeLimitOrder.timestamp?.time,
        )
    }
}
