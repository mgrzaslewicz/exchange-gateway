package com.autocoin.exchangegateway.spi.exchange.ticker.listener

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.ticker.Ticker


interface TickerListener {

    fun onTicker(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        ticker: Ticker,
    )

    /**
     * There was no new ticker on exchange but time has passed
     * @param ticker might be the same that was already fetched from exchange or none
     */
    fun onNoNewTicker(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        ticker: Ticker?,
    ) {
    }

}
