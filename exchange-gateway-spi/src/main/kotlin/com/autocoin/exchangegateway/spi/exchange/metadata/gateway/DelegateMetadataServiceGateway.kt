package com.autocoin.exchangegateway.spi.exchange.metadata.gateway

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata
import com.autocoin.exchangegateway.spi.exchange.metadata.service.MetadataService

class DelegateMetadataServiceGateway<T>(
    private val metadataServiceGateways: Map<ExchangeName, MetadataService<T>>,
) : MetadataServiceGateway<T> {

    override fun getMetadata(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): ExchangeMetadata {
        return metadataServiceGateways
            .getValue(exchangeName)
            .getMetadata(apiKey = apiKey)
    }

}
