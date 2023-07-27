package com.autocoin.exchangegateway.spi.exchange.metadata.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata

interface MetadataServiceGateway<T> {

    fun getMetadata(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): ExchangeMetadata

}
