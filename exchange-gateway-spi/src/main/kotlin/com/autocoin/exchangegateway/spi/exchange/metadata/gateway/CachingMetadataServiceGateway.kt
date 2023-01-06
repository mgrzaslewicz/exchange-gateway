package com.autocoin.exchangegateway.spi.exchange.metadata.gateway

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata
import java.util.concurrent.ConcurrentHashMap

class CachingMetadataServiceGateway<T>(private val decorated: MetadataServiceGateway<T>) : MetadataServiceGateway<T> {
    private val locks = ConcurrentHashMap<ExchangeName, Any>()
    private val cache = ConcurrentHashMap<ExchangeName, ExchangeMetadata>()

    fun refreshMetadata(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ) {
        synchronized(locks.computeIfAbsent(exchangeName) { Any() }) {
            cache.remove(exchangeName)
            getMetadata(
                exchangeName = exchangeName,
                apiKey = apiKey,
            )
        }
    }

    override fun getMetadata(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): ExchangeMetadata {
        synchronized(locks.computeIfAbsent(exchangeName) { Any() }) {
            return cache.computeIfAbsent(exchangeName) {
                decorated.getMetadata(
                    exchangeName = exchangeName,
                    apiKey = apiKey,
                )
            }
        }
    }

}
