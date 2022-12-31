package com.autocoin.exchangegateway.spi.exchange.apikey

import java.util.function.Supplier

class CachingApiKeySupplier(private val decorated: Supplier<ApiKey>) :
    Supplier<ApiKey> {
    private var cachedApiKey: ApiKey? = null
    override fun get(): ApiKey {
        synchronized(this) {
            if (cachedApiKey == null) {
                cachedApiKey = decorated.get()
            }
        }
        return cachedApiKey!!
    }
}

fun Supplier<ApiKey>.caching() = CachingApiKeySupplier(this)
