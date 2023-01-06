package com.autocoin.exchangegateway.spi.exchange.metadata.service.authorized

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface AuthorizedMetadataServiceFactory<T> {

    fun createAuthorizeMetadataService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedMetadataService<T>

}
