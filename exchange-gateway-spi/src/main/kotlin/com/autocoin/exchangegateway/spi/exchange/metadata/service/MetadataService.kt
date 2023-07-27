package com.autocoin.exchangegateway.spi.exchange.metadata.service

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata

interface MetadataService<T> {
    val exchange: Exchange
    fun getMetadata(
        apiKey: ApiKeySupplier<T>,
    ): ExchangeMetadata
}
