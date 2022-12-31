package com.autocoin.exchangegateway.spi.exchange.ticker.service

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface TickerServiceFactory<T> {
    fun createTickerService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): TickerService<T>

}
