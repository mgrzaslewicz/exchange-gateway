package com.autocoin.exchangegateway.spi.exchange

import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface AuthorizedService<T> {
    val exchangeName: ExchangeName

    val apiKey: ApiKeySupplier<T>
}
