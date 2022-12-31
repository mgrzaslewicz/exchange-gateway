package com.autocoin.exchangegateway.spi.exchange.metadata.gateway

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKey
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata
import java.util.function.Supplier

interface MetadataServiceGateway {
    fun refreshMetadata(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>?,
    )

    fun getMetadata(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>?,
    ): ExchangeMetadata

}
