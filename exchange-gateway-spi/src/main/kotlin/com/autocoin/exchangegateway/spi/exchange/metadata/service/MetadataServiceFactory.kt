package com.autocoin.exchangegateway.spi.exchange.metadata.service

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import java.util.function.Supplier

interface MetadataServiceFactory {
    fun createMetadataService(
        exchangeName: ExchangeName,
        apiKey: Supplier<com.autocoin.exchangegateway.spi.exchange.apikey.ApiKey>,
    ): MetadataService

}
