package com.autocoin.exchangegateway.api.exchange.orderbook

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderBook
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
