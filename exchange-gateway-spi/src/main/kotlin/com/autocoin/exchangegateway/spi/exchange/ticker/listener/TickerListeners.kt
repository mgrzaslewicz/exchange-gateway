package com.autocoin.exchangegateway.spi.exchange.ticker.listener

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair


interface TickerListeners {
    fun addTickerListener(
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
        listener: TickerListener,
    ): Boolean

    fun removeTickerListener(
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
        listener: TickerListener,
    ): Boolean

    fun addTickerRegistrationListener(tickerRegistrationListener: TickerRegistrationListener)
    fun removeTickerRegistrationListener(tickerRegistrationListener: TickerRegistrationListener)
    fun getTickerListeners(exchangeName: ExchangeName): Map<CurrencyPair, Set<TickerListener>>
}
