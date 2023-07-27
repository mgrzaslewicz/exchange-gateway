package com.autocoin.exchangegateway.api.exchange.metadata.gateway

import com.autocoin.exchangegateway.api.keyvalue.LatestVersion
import com.autocoin.exchangegateway.spi.exchange.Exchange
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
    private val metadataRepository: KeyValueRepository<LatestVersion<SpiExchangeMetadata>, Exchange, SpiExchangeMetadata>,
) : MetadataServiceGateway<T> {

    companion object : KLogging()

    override fun getMetadata(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): SpiExchangeMetadata {
        return getAndSaveExchangeMetadata(exchange, apiKey)
    }

    private fun getAndSaveExchangeMetadata(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): SpiExchangeMetadata {
        logger.debug { "[$exchange] Getting exchange metadata" }
        val result = metadataRepository.getLatestVersion(exchange)
        return if (result != null) {
            result.value
        }
        else {
            logger.info { "[$exchange] Fetching exchange metadata" }
            return fetchAndSaveExchangeMetadata(
                exchange = exchange,
                apiKey = apiKey,
            )
        }
    }

    private fun fetchAndSaveExchangeMetadata(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): SpiExchangeMetadata {
        val freshExchangeMetadata = decorated.getMetadata(
            exchange = exchange,
            apiKey = apiKey,
        )
        metadataRepository.saveNewVersion(exchange, freshExchangeMetadata)
        return freshExchangeMetadata
    }

}
