package com.autocoin.exchangegateway.spi.exchange.ticker.listener

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair

interface TickerRegistrationListener {
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
