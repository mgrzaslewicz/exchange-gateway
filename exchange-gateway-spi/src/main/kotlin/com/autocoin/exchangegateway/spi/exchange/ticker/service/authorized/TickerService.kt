package com.autocoin.exchangegateway.spi.exchange.ticker.service.authorized

import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.ticker.Ticker

interface AuthorizedTickerService<T> : com.autocoin.exchangegateway.spi.exchange.AuthorizedService<T> {
    fun getTicker(currencyPair: CurrencyPair): Ticker
    fun getTickers(currencyPairs: Collection<CurrencyPair>): List<Ticker>
}

