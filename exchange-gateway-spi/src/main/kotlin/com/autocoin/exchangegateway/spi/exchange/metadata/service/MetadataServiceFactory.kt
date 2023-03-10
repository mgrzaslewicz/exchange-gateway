package com.autocoin.exchangegateway.spi.exchange.metadata.service

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface MetadataServiceFactory<T> {
    fun createMetadataService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): MetadataService<T>

}
