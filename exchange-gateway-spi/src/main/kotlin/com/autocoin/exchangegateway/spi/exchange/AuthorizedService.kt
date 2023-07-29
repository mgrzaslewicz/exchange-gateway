package com.autocoin.exchangegateway.spi.exchange

import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier


/**
 * Service authorized for specific api key
 */
interface AuthorizedService<T> {
    val exchange: Exchange

    val apiKey: ApiKeySupplier<T>
}
