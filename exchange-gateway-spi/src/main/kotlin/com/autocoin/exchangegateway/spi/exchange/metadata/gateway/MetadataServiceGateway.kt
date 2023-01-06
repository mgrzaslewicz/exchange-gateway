package com.autocoin.exchangegateway.spi.exchange.metadata.gateway

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata

interface MetadataServiceGateway<T> {

    fun getMetadata(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): ExchangeMetadata

}
