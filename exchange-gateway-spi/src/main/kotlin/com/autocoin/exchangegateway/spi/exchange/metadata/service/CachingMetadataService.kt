package com.autocoin.exchangegateway.spi.exchange.metadata.service

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata
import java.util.concurrent.atomic.AtomicReference

class CachingMetadataService<T>(private val decorated: MetadataService<T>) : MetadataService<T> {
    private val lock = Any()
    private val cache = AtomicReference<ExchangeMetadata>()

    override val exchange: Exchange = decorated.exchange
    fun refreshMetadata(
        apiKey: ApiKeySupplier<T>,
    ) {
        synchronized(lock) {
            cache.set(decorated.getMetadata(apiKey))
        }
    }

    override fun getMetadata(
        apiKey: ApiKeySupplier<T>,
    ): ExchangeMetadata {
        synchronized(lock) {
            return cache.get() ?: decorated.getMetadata(apiKey = apiKey).also { cache.set(it) }
        }
    }

}
