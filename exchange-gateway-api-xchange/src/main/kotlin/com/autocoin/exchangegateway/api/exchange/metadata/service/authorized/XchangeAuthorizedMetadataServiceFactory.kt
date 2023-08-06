package com.autocoin.exchangegateway.api.exchange.metadata.service.authorized

import com.autocoin.exchangegateway.api.exchange.xchange.XchangeProvider
import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.metadata.service.authorized.AuthorizedMetadataService
import com.autocoin.exchangegateway.spi.exchange.metadata.service.authorized.AuthorizedMetadataServiceFactory

class XchangeAuthorizedMetadataServiceFactory<T>(
    private val xchangeProvider: XchangeProvider<T>,
) : AuthorizedMetadataServiceFactory<T> {
    override fun createAuthorizedMetadataService(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedMetadataService<T> {
        val xchange = xchangeProvider(
            exchange = exchange,
            apiKey = apiKey,
        )
        return XchangeAuthorizedMetadataService(
            exchange = exchange,
            apiKey = apiKey,
            delegate = xchange,
        )
    }
}
