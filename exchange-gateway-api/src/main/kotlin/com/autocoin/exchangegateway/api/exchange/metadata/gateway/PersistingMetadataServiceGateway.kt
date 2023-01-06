package com.autocoin.exchangegateway.api.exchange.metadata.gateway

import com.autocoin.exchangegateway.api.keyvalue.LatestVersion
import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.metadata.gateway.MetadataServiceGateway
import com.autocoin.exchangegateway.spi.keyvalue.KeyValueRepository
import mu.KLogging
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata as SpiExchangeMetadata

/**
 * Will fetch and save metadata if there is none for given exchange yet
 */
class PersistingMetadataServiceGateway<T>(
    private val decorated: MetadataServiceGateway<T>,
    private val metadataRepository: KeyValueRepository<LatestVersion<SpiExchangeMetadata>, ExchangeName, SpiExchangeMetadata>,
) : MetadataServiceGateway<T> {

    companion object : KLogging()

    override fun getMetadata(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): SpiExchangeMetadata {
        return getAndSaveExchangeMetadata(exchangeName, apiKey)
    }

    private fun getAndSaveExchangeMetadata(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): SpiExchangeMetadata {
        logger.debug { "[$exchangeName] Getting exchange metadata" }
        val result = metadataRepository.getLatestVersion(exchangeName)
        return if (result != null) {
            result.value
        }
        else {
            logger.info { "[$exchangeName] Fetching exchange metadata" }
            return fetchAndSaveExchangeMetadata(
                exchangeName = exchangeName,
                apiKey = apiKey,
            )
        }
    }

    private fun fetchAndSaveExchangeMetadata(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): SpiExchangeMetadata {
        val freshExchangeMetadata = decorated.getMetadata(
            exchangeName = exchangeName,
            apiKey = apiKey,
        )
        metadataRepository.saveNewVersion(exchangeName, freshExchangeMetadata)
        return freshExchangeMetadata
    }

}
