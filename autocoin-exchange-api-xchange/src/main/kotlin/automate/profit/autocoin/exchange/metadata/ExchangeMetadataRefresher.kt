package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.apikey.ServiceApiKeysProvider
import mu.KLogging
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

class ExchangeMetadataRefresher(
    exchangeMetadataFetchers: List<ExchangeMetadataFetcher>,
    private val exchangeMetadataRepository: FileExchangeMetadataRepository,
    private val serviceApiKeysProvider: ServiceApiKeysProvider,
    private val cachingExchangeMetadataService: CachingExchangeMetadataService,
    private val executorService: ExecutorService
) {

    init {
        if (exchangeMetadataFetchers.map { it.supportedExchange }.toSet().size < exchangeMetadataFetchers.size) {
            throw IllegalStateException("Provided fetchers are invalid, there are duplicated ones")
        }
    }

    companion object : KLogging()

    private val fetchersMap = exchangeMetadataFetchers.associateBy { it.supportedExchange }

    /**
     * Schedule this refresh every x minutes/hours to have fresh metadata
     */
    fun fetchAndSaveFreshMetadataFiles() {
        logger.info { "Refreshing all supported exchange metadata files" }
        val saveMetadataJobs = SupportedExchange.values().map { supportedExchange ->
            executorService.submit {
                logger.info { "[$supportedExchange] Refreshing metadata" }
                try {
                    val apiKeys = serviceApiKeysProvider.getApiKeys(supportedExchange)
                    val freshExchangeMetadata = fetchersMap.getValue(supportedExchange).fetchExchangeMetadata(apiKeys)
                    exchangeMetadataRepository.saveExchangeMetadata(supportedExchange, freshExchangeMetadata)
                    exchangeMetadataRepository.keepLastNBackups(supportedExchange, maxBackups = 100)
                    cachingExchangeMetadataService.refreshMetadata(supportedExchange.exchangeName)
                    logger.info { "[$supportedExchange] Refreshing metadata finished" }
                } catch (e: Exception) {
                    logger.error(e) { "[$supportedExchange] Exception during metadata refresh" }
                } catch (e: Throwable/* catch runtime class errors too */) {
                    logger.error(e) { "[$supportedExchange] Error during metadata refresh" }
                }
            }
        }
        saveMetadataJobs.forEach {
            try {
                it.get(15, TimeUnit.MINUTES)
            } catch (e: Exception) {
                logger.error(e) { "Error during waiting for metadata refresh to finish" }
            }
        }
        logger.info { "Refreshing all supported exchange metadata files finished" }
    }

}
