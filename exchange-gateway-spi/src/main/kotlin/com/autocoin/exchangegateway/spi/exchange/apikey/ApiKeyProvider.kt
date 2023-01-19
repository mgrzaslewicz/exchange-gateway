package com.autocoin.exchangegateway.spi.exchange.apikey

interface ApiKeyProvider<T> {
    fun getApiKey(apiKeyId: T): ApiKeySupplier<T>
}
