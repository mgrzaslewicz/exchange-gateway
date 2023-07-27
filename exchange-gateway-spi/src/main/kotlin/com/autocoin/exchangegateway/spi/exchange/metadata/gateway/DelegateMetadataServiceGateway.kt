package com.autocoin.exchangegateway.spi.exchange.metadata.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata
import com.autocoin.exchangegateway.spi.exchange.metadata.service.MetadataService

class DelegateMetadataServiceGateway<T>(
    private val metadataServiceGateways: Map<Exchange, MetadataService<T>>,
) : MetadataServiceGateway<T> {

    override fun getMetadata(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): ExchangeMetadata {
        return metadataServiceGateways
            .getValue(exchange)
            .getMetadata(apiKey = apiKey)
    }

}
