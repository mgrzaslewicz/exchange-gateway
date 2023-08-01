package com.autocoin.exchangegateway.spi.exchange.ticker.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.ticker.Ticker


interface AuthorizedTickerServiceGateway {
    fun getTicker(
        exchange: Exchange,
        currencyPair: CurrencyPair,
    ): Ticker

    fun getTickers(
        exchangeName: Exchange,
        currencyPairs: Collection<CurrencyPair>,
    ): List<Ticker>
}


