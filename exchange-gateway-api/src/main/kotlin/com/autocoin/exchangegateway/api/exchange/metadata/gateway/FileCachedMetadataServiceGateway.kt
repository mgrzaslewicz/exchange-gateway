package com.autocoin.exchangegateway.api.exchange.metadata.gateway

import com.autocoin.exchangegateway.api.exchange.metadata.repository.ExchangeMetadataResult
import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKey
import com.autocoin.exchangegateway.spi.exchange.metadata.gateway.MetadataServiceGateway
import com.autocoin.exchangegateway.spi.keyvalue.KeyValueRepository
import mu.KLogging
import java.util.function.Supplier
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata as SpiExchangeMetadata

/**
 * Will fetch and save metadata if there is none for given exchange yet
 */
class FileCachedMetadataServiceGateway(
    private val decorated: MetadataServiceGateway,
    private val metadataRepository: KeyValueRepository<SpiExchangeMetadata, String, SpiExchangeMetadata>,
) : MetadataServiceGateway {

    companion object : KLogging()

    override fun getMetadata(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>?,
    ): SpiExchangeMetadata {
        return getAndSaveExchangeMetadata(exchangeName, apiKey)
    }

    private fun getAndSaveExchangeMetadata(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>?,
    ): SpiExchangeMetadata {
        logger.debug { "[$exchangeName] Getting exchange metadata" }
        val result = metadataRepository.getLatestVersion(exchangeName.value)
        return if (result != null) {
            result
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
        apiKey: Supplier<ApiKey>?,
    ): SpiExchangeMetadata {
        val freshExchangeMetadata = decorated.getMetadata(
            exchangeName = exchangeName,
            apiKey = apiKey,
        )
        metadataRepository.saveNewVersion(exchangeName.value, freshExchangeMetadata)
        return freshExchangeMetadata
    }

    private fun logGettingMetadataError(exchangeMetadataResult: ExchangeMetadataResult) {
        if (exchangeMetadataResult.hasException()) {
            logger.error(exchangeMetadataResult.exception) { "Exception during loading metadata from storage" }
        }
    }

    override fun refreshMetadata(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>?,
    ) {
        logger.info { "[$exchangeName] Refreshing exchange metadata" }
        fetchAndSaveExchangeMetadata(
            exchangeName = exchangeName,
            apiKey = apiKey,
        )
    }
}
