package com.autocoin.exchangegateway.spi.exchange.metadata.gateway

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKey
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata
import com.autocoin.exchangegateway.spi.exchange.metadata.service.MetadataService
import java.util.function.Supplier

class DelegateMetadataServiceGateway(
    private val metadataServiceGateways: Map<ExchangeName, MetadataService>,
) : MetadataServiceGateway {

    override fun refreshMetadata(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>?,
    ) {
        metadataServiceGateways.getValue(exchangeName).refreshMetadata()
    }

    override fun getMetadata(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>?,
    ): ExchangeMetadata {
        return metadataServiceGateways.getValue(exchangeName).getMetadata()
    }

}
