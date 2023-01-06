package com.autocoin.exchangegateway.spi.exchange.metadata.gateway

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata
import com.autocoin.exchangegateway.spi.exchange.metadata.service.authorized.AuthorizedMetadataServiceFactory

class MetadataServiceGatewayUsingAuthorizedMetadataService<T>(
    private val authorizedMetadataServiceFactory: AuthorizedMetadataServiceFactory<T>,
) : MetadataServiceGateway<T> {
    override fun getMetadata(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): ExchangeMetadata {
        return authorizedMetadataServiceFactory
            .createAuthorizeMetadataService(
                exchangeName = exchangeName,
                apiKey = apiKey,
            )
            .getMetadata()
    }
}
