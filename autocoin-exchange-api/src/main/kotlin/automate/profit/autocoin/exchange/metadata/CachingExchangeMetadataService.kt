package automate.profit.autocoin.exchange.metadata

import java.util.concurrent.ConcurrentHashMap

class CachingExchangeMetadataService(private val decorated: ExchangeMetadataService) : ExchangeMetadataService {
    private val locks = ConcurrentHashMap<String, Any>()
    private val cache = ConcurrentHashMap<String, ExchangeMetadata>()

    fun refreshMetadata(exchangeName: String) {
        synchronized(locks.computeIfAbsent(exchangeName) { Any() }) {
            cache.remove(exchangeName)
            getMetadata(exchangeName)
        }
    }

    override fun getMetadata(exchangeName: String): ExchangeMetadata {
        synchronized(locks.computeIfAbsent(exchangeName) { Any() }) {
            return cache.computeIfAbsent(exchangeName) { decorated.getMetadata(exchangeName) }
        }
    }

}
