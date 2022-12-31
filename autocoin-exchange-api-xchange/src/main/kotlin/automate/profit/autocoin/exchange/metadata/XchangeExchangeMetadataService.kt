package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.peruser.UserExchangeServicesFactory
import mu.KLogging

class XchangeExchangeMetadataService(
        private val userExchangeServicesFactory: UserExchangeServicesFactory) : ExchangeMetadataService {
    companion object : KLogging()

    private val metadataCache = mutableMapOf<String, ExchangeMetadata>()

    override fun getMetadata(exchangeName: String): ExchangeMetadata {
        logger.info { "Requesting exchange metadata for $exchangeName" }
        return metadataCache.computeIfAbsent(exchangeName) {
            userExchangeServicesFactory.createMetadataProvider(exchangeName).exchangeMetadata
        }
    }

}
