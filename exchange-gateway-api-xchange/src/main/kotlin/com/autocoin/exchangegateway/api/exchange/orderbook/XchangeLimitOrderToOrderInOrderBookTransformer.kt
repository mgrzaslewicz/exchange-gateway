package com.autocoin.exchangegateway.api.exchange.orderbook

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide
import org.knowm.xchange.dto.trade.LimitOrder
import java.util.function.Function
import org.knowm.xchange.currency.CurrencyPair as XchangeCurrencyPair
import org.knowm.xchange.dto.Order as XchangeOrder

interface XchangeLimitOrderToOrderInOrderBookTransformer {
    operator fun invoke(
        xchangeLimitOrder: LimitOrder,
        exchangeName: ExchangeName,
        receivedAtMillis: Long,
        xchangeOrderTypeTransformer: Function<XchangeOrder.OrderType, OrderSide>,
        xchangeCurrencyPairTransformer: Function<XchangeCurrencyPair, CurrencyPair>,
    ): OrderInOrderBook
}
