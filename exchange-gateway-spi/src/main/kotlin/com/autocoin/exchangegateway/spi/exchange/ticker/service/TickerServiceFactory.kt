package com.autocoin.exchangegateway.spi.exchange.ticker.service

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface TickerServiceFactory<T> {
    fun createTickerService(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): TickerService<T>

}
