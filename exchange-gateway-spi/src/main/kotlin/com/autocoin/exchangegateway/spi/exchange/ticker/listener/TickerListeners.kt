package com.autocoin.exchangegateway.spi.exchange.ticker.listener

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair


interface TickerListeners {
    fun addTickerListener(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        listener: TickerListener,
    ): Boolean

    fun removeTickerListener(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        listener: TickerListener,
    ): Boolean

    fun addTickerRegistrationListener(tickerRegistrationListener: TickerRegistrationListener)
    fun removeTickerRegistrationListener(tickerRegistrationListener: TickerRegistrationListener)
    fun getTickerListeners(exchange: Exchange): Map<CurrencyPair, Set<TickerListener>>
}
