package com.autocoin.exchangegateway.api.exchange.apikey

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKey
import java.util.function.Supplier

/**
 * Provides API keys where these are not provided by user and needed
 * for reading public data like ticker, order book, metadata
 */
interface ApiKeysProvider {
    fun getApiKey(exchangeName: ExchangeName): Supplier<ApiKey>?
}
