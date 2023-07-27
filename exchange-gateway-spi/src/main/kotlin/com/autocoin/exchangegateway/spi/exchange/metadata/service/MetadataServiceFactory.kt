package com.autocoin.exchangegateway.spi.exchange.metadata.service

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface MetadataServiceFactory<T> {
    fun createMetadataService(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): MetadataService<T>

}
