package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.apikey.ExchangeKeyService
import automate.profit.autocoin.exchange.apikey.ExchangeService
import automate.profit.autocoin.exchange.peruser.UserExchangeServicesFactory
import mu.KLogging

class XchangeExchangeMetadataService(private val exchangeService: ExchangeService,
                                     private val exchangeKeyService: ExchangeKeyService,
                                     private val userExchangeServicesFactory: UserExchangeServicesFactory) : ExchangeMetadataService {
    companion object : KLogging()

    private val metadataCache = mutableMapOf<String, ExchangeMetadata>()

    override fun getMetadata(exchangeName: String, exchangeUserId: String): ExchangeMetadata {
        logger.info { "Requesting exchange metadata for $exchangeName and user $exchangeUserId" }
        val key = "$exchangeName.$exchangeUserId"
        return metadataCache.computeIfAbsent(key) {
            val exchangeId = exchangeService.getExchangeIdByName(exchangeName)
            val exchangeKey = exchangeKeyService.getExchangeKey(exchangeUserId, exchangeId)
                    ?: throw IllegalArgumentException("Could not create exchange metadata provider. Exchange key for Exchange(name=$exchangeName,id=$exchangeId) and exchangeUserId=$exchangeUserId not found")
            userExchangeServicesFactory
                    .createMetadataProvider(exchangeName, exchangeKey.apiKey, exchangeKey.secretKey, exchangeKey.userName)
                    .exchangeMetadata
        }
    }

    override fun getMetadata(exchangeName: String): ExchangeMetadata {
        logger.info { "Requesting exchange metadata for $exchangeName" }
        val key = exchangeName
        return metadataCache.computeIfAbsent(key) {
            userExchangeServicesFactory.createMetadataProvider(exchangeName).exchangeMetadata
        }
    }

}
