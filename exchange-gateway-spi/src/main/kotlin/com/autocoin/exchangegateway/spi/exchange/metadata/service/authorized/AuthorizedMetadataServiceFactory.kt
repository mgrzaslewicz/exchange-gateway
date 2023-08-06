package com.autocoin.exchangegateway.spi.exchange.metadata.service.authorized

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface AuthorizedMetadataServiceFactory<T> {

    fun createAuthorizedMetadataService(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedMetadataService<T>

}
