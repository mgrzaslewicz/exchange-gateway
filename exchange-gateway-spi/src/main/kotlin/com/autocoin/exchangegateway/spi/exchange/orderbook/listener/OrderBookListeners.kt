package com.autocoin.exchangegateway.spi.exchange.orderbook.listener

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair


interface OrderBookListeners {
    fun addOrderBookListener(
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
        listener: OrderBookListener,
    ): Boolean

    fun removeOrderBookListener(
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
        listener: OrderBookListener,
    ): Boolean

    fun addOrderBookRegistrationListener(orderBookRegistrationListener: OrderBookRegistrationListener)
    fun removeOrderBookRegistrationListener(orderBookRegistrationListener: OrderBookRegistrationListener)
    fun getOrderBookListeners(exchangeName: ExchangeName): Map<CurrencyPair, Set<OrderBookListener>>
}
