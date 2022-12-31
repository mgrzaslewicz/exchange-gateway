package automate.profit.autocoin.spi.exchange.metadata.service

import automate.profit.autocoin.spi.exchange.metadata.ExchangeMetadata
import java.util.concurrent.atomic.AtomicReference

class CachingMetadataService(private val decorated: MetadataService) : MetadataService {
    private val lock = Any()
    private val cache = AtomicReference<ExchangeMetadata>()

    override val exchangeName = decorated.exchangeName
    override fun refreshMetadata() {
        synchronized(lock) {
            cache.set(decorated.getMetadata())
        }
    }

    override fun getMetadata(): ExchangeMetadata {
        synchronized(lock) {
            return cache.get() ?: decorated.getMetadata().also { cache.set(it) }
        }
    }

}
