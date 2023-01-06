package com.autocoin.exchangegateway.api.exchange.xchange

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import java.util.concurrent.ConcurrentHashMap
import org.knowm.xchange.Exchange as XchangeExchange

interface ApiKeyToCacheKeyProvider<T, K> {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): K
}

/**
 * @param T the type of the API key ID
 * @param K the type of cache key for ApiKeySupplier<T>
 */
class CachingXchangeProvider<T, K>(
    private val decorated: XchangeProvider<T>,
    private val apiKeyToCacheKey: ApiKeyToCacheKeyProvider<T, K>,
) : XchangeProvider<T> {
    private val cache = ConcurrentHashMap<K, XchangeExchange>()

    override fun invoke(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): XchangeExchange {
        val cacheKey = apiKeyToCacheKey(
            exchangeName = exchangeName,
            apiKey = apiKey,
        )
        return cache.computeIfAbsent(cacheKey) {
            decorated(
                exchangeName = exchangeName,
                apiKey = apiKey,
            )
        }
    }
}
