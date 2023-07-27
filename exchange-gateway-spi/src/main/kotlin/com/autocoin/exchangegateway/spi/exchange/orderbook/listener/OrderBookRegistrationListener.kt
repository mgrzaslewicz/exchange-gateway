package com.autocoin.exchangegateway.spi.exchange.orderbook.listener

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair

interface OrderBookRegistrationListener {
    fun onLastListenerDeregistered(exchange: Exchange)
    fun onListenerDeregistered(
        exchange: Exchange,
        currencyPair: CurrencyPair,
    )

    fun onFirstListenerRegistered(exchange: Exchange)
    fun onListenerRegistered(
        exchange: Exchange,
        currencyPair: CurrencyPair,
    )
}
