package com.autocoin.exchangegateway.spi.exchange.orderbook.listener

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair


interface OrderBookListeners {
    fun addOrderBookListener(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        listener: OrderBookListener,
    ): Boolean

    fun removeOrderBookListener(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        listener: OrderBookListener,
    ): Boolean

    fun addOrderBookRegistrationListener(orderBookRegistrationListener: OrderBookRegistrationListener)
    fun removeOrderBookRegistrationListener(orderBookRegistrationListener: OrderBookRegistrationListener)
    fun getOrderBookListeners(exchange: Exchange): Map<CurrencyPair, Set<OrderBookListener>>
}
