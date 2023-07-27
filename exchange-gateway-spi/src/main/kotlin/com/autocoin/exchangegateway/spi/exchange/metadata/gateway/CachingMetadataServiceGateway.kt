package com.autocoin.exchangegateway.spi.exchange.metadata.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata
import java.util.concurrent.ConcurrentHashMap

class CachingMetadataServiceGateway<T>(private val decorated: MetadataServiceGateway<T>) : MetadataServiceGateway<T> {
    private val locks = ConcurrentHashMap<Exchange, Any>()
    private val cache = ConcurrentHashMap<Exchange, ExchangeMetadata>()

    fun refreshMetadata(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ) {
        synchronized(locks.computeIfAbsent(exchange) { Any() }) {
            cache.remove(exchange)
            getMetadata(
                exchange = exchange,
                apiKey = apiKey,
            )
        }
    }

    override fun getMetadata(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): ExchangeMetadata {
        synchronized(locks.computeIfAbsent(exchange) { Any() }) {
            return cache.computeIfAbsent(exchange) {
                decorated.getMetadata(
                    exchange = exchange,
                    apiKey = apiKey,
                )
            }
        }
    }

}
