package com.autocoin.exchangegateway.spi.exchange.metadata.service

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata

interface MetadataService<T> {
    val exchangeName: ExchangeName
    fun getMetadata(
        apiKey: ApiKeySupplier<T>,
    ): ExchangeMetadata
}
