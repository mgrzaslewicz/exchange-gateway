package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import mu.KLogging

class ExchangeMetadataRefresher(
        exchangeMetadataFetchers: List<ExchangeMetadataFetcher>,
        private val exchangeMetadataRepository: FileExchangeMetadataRepository
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
            logger.info { "Refreshing $supportedExchange" }
            try {
                val (xchangeMetadataJson, freshExchangeMetadata) = fetchersMap.getValue(supportedExchange).fetchExchangeMetadata()
                exchangeMetadataRepository.saveExchangeMetadata(supportedExchange, freshExchangeMetadata, xchangeMetadataJson)
            } catch(e: Exception) {
                logger.error(e) { "Exception during metadata refresh for $supportedExchange" }
            }
            catch (e: Throwable/* catch runtime class errors too */) {
                logger.error(e) { "Error during metadata refresh for $supportedExchange" }
            }
        }
    }

}
