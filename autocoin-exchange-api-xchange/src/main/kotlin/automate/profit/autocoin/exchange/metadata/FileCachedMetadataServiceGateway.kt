package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.metadata.ExchangeMetadata
import automate.profit.autocoin.spi.exchange.metadata.gateway.MetadataServiceGateway
import mu.KLogging
import java.util.function.Supplier

/**
 * Will fetch and save metadata if there is none for given exchange yet
 */
class FileCachedMetadataServiceGateway(
    private val decorated: MetadataServiceGateway,
    private val exchangeMetadataRepository: ExchangeMetadataRepository,
) : MetadataServiceGateway {

    companion object : KLogging()

    override fun getMetadata(exchangeName: ExchangeName, apiKey: Supplier<ApiKey?>): ExchangeMetadata {
        return getAndSaveExchangeMetadata(exchangeName, apiKey)
    }

    private fun getAndSaveExchangeMetadata(exchangeName: ExchangeName, apiKey: Supplier<ApiKey?>): ExchangeMetadata {
        logger.debug { "[$exchangeName] Getting exchange metadata" }
        val result = exchangeMetadataRepository.getLatestExchangeMetadata(exchangeName)
        return if (result.hasMetadata()) {
            result.exchangeMetadata!!
        } else {
            logGettingMetadataError(result)
            logger.info { "[$exchangeName] Fetching exchange metadata" }
            return fetchAndSaveExchangeMetadata(
                exchangeName = exchangeName,
                apiKey = apiKey,
            )
        }
    }

    private fun fetchAndSaveExchangeMetadata(exchangeName: ExchangeName, apiKey: Supplier<ApiKey?>): ExchangeMetadata {
        val freshExchangeMetadata = decorated.getMetadata(
            exchangeName = exchangeName,
            apiKey = apiKey,
        )
        exchangeMetadataRepository.saveExchangeMetadata(exchangeName, freshExchangeMetadata)
        return freshExchangeMetadata
    }

    private fun logGettingMetadataError(exchangeMetadataResult: ExchangeMetadataResult) {
        if (exchangeMetadataResult.hasException()) {
            logger.error(exchangeMetadataResult.exception) { "Exception during loading metadata from storage" }
        }
    }

    override fun refreshMetadata(exchangeName: ExchangeName, apiKey: Supplier<ApiKey?>) {
        logger.info { "[$exchangeName] Refreshing exchange metadata" }
        fetchAndSaveExchangeMetadata(
            exchangeName = exchangeName,
            apiKey = apiKey,
        )
    }
}
