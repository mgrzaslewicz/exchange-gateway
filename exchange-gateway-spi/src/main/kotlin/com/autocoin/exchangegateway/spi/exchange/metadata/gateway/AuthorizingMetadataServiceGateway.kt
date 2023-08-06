package com.autocoin.exchangegateway.spi.exchange.metadata.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata
import com.autocoin.exchangegateway.spi.exchange.metadata.service.authorized.AuthorizedMetadataServiceFactory

class AuthorizingMetadataServiceGateway<T>(
    private val authorizedMetadataServiceFactory: AuthorizedMetadataServiceFactory<T>,
) : MetadataServiceGateway<T> {
    override fun getMetadata(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): ExchangeMetadata {
        return authorizedMetadataServiceFactory
            .createAuthorizedMetadataService(
                exchange = exchange,
                apiKey = apiKey,
            )
            .getMetadata()
    }
}
