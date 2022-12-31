package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.apikey.ServiceApiKeysProvider
import mu.KLogging

/**
 * Will fetch and save metadata if there is none for given exchange yet
 */
class DefaultExchangeMetadataService(
    exchangeMetadataFetchers: List<ExchangeMetadataFetcher>,
    private val exchangeMetadataRepository: FileExchangeMetadataRepository,
    private val serviceApiKeysProvider: ServiceApiKeysProvider
) : ExchangeMetadataService {

    init {
        check(exchangeMetadataFetchers
            .map { it.supportedExchange }
            .toSet().size >= exchangeMetadataFetchers.size
        ) { "Provided fetchers are invalid, there are duplicated ones" }
    }

    companion object : KLogging()

    override fun getMetadata(exchangeName: String): ExchangeMetadata {
        return getAndSaveExchangeMetadata(SupportedExchange.fromExchangeName(exchangeName))
    }

    private val fetchersMap = exchangeMetadataFetchers.associateBy { it.supportedExchange }

    fun getAndSaveExchangeMetadata(supportedExchange: SupportedExchange): ExchangeMetadata {
        logger.debug { "[$supportedExchange] Getting  exchange metadata" }
        val exchangeMetadataResult = exchangeMetadataRepository.getLatestExchangeMetadata(supportedExchange)
        return if (exchangeMetadataResult.hasMetadata()) {
            exchangeMetadataResult.exchangeMetadata!!
        } else {
            logGettingMetadataError(exchangeMetadataResult)
            logger.info { "[$supportedExchange] Fetching exchange metadata" }
            val apiKey = serviceApiKeysProvider.getApiKeys(supportedExchange)
            val freshExchangeMetadata = fetchersMap.getValue(supportedExchange).fetchExchangeMetadata(apiKey)
            exchangeMetadataRepository.saveExchangeMetadata(supportedExchange, freshExchangeMetadata)
            return freshExchangeMetadata
        }
    }

    private fun logGettingMetadataError(exchangeMetadataResult: ExchangeMetadataResult) {
        if (exchangeMetadataResult.hasException()) {
            logger.error(exchangeMetadataResult.exception) { "Exception during loading metadata from storage" }
        }
    }

}
