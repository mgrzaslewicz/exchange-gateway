package com.autocoin.exchangegateway.spi.exchange.ticker.listener

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair

interface TickerRegistrationListener {
    fun onLastListenerDeregistered(exchangeName: ExchangeName)
    fun onListenerDeregistered(
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
    )

    fun onFirstListenerRegistered(exchangeName: ExchangeName)
    fun onListenerRegistered(
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
    )
}
