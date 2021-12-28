package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.apikey.ServiceApiKeysProvider
import mu.KLogging

class ExchangeMetadataRefresher(
        exchangeMetadataFetchers: List<ExchangeMetadataFetcher>,
        private val exchangeMetadataRepository: FileExchangeMetadataRepository,
        private val serviceApiKeysProvider: ServiceApiKeysProvider
) {

    init {
        if (exchangeMetadataFetchers.map { it.supportedExchange }.toSet().size < exchangeMetadataFetchers.size) {
            throw IllegalStateException("Provided fetchers are invalid, there are duplicated ones")
        }
    }

    companion object : KLogging()

    private val fetchersMap = exchangeMetadataFetchers.map { it.supportedExchange to it }.toMap()

    /**
     * Schedule this refresh every x minutes/hours to have fresh metadata
     */
    fun fetchAndSaveFreshMetadataFiles() {
        logger.info { "Refreshing all supported exchange metadata files" }
        SupportedExchange.values().forEach { supportedExchange ->
            logger.info { "[$supportedExchange] Refreshing metadata" }
            try {
                val apiKeys = serviceApiKeysProvider.getApiKeys(supportedExchange)
                val freshExchangeMetadata = fetchersMap.getValue(supportedExchange).fetchExchangeMetadata(apiKeys)
                exchangeMetadataRepository.saveExchangeMetadata(supportedExchange, freshExchangeMetadata)
                exchangeMetadataRepository.keepLastNBackups(supportedExchange, maxBackups = 100)
            } catch (e: Exception) {
                logger.error(e) { "[$supportedExchange] Exception during metadata refresh" }
            } catch (e: Throwable/* catch runtime class errors too */) {
                logger.error(e) { "[$supportedExchange] Error during metadata refresh" }
            }
        }
    }

}
