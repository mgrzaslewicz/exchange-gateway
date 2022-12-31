package automate.profit.autocoin.spi.exchange.metadata.gateway

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.metadata.ExchangeMetadata
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Supplier

class CachingMetadataServiceGateway(private val decorated: MetadataServiceGateway) : MetadataServiceGateway {
    private val locks = ConcurrentHashMap<ExchangeName, Any>()
    private val cache = ConcurrentHashMap<ExchangeName, ExchangeMetadata>()

    override fun refreshMetadata(exchangeName: ExchangeName, apiKey: Supplier<ApiKey?>) {
        synchronized(locks.computeIfAbsent(exchangeName) { Any() }) {
            cache.remove(exchangeName)
            getMetadata(
                exchangeName = exchangeName,
                apiKey = apiKey,
            )
        }
    }

    override fun getMetadata(exchangeName: ExchangeName, apiKey: Supplier<ApiKey?>): ExchangeMetadata {
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
