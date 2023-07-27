package com.autocoin.exchangegateway.spi.exchange

import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface AuthorizedService<T> {
    val exchange: Exchange

    val apiKey: ApiKeySupplier<T>
}
