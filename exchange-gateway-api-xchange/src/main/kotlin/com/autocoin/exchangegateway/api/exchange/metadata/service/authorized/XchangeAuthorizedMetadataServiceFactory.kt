package com.autocoin.exchangegateway.api.exchange.metadata.service.authorized

import com.autocoin.exchangegateway.api.exchange.xchange.XchangeProvider
import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.metadata.service.authorized.AuthorizedMetadataService
import com.autocoin.exchangegateway.spi.exchange.metadata.service.authorized.AuthorizedMetadataServiceFactory

class XchangeAuthorizedMetadataServiceFactory<T>(
    private val xchangeProvider: XchangeProvider<T>,
) : AuthorizedMetadataServiceFactory<T> {
    override fun createAuthorizeMetadataService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedMetadataService<T> {
        val xchange = xchangeProvider(
            exchangeName = exchangeName,
            apiKey = apiKey,
        )
        return XchangeAuthorizedMetadataService(
            exchangeName = exchangeName,
            apiKey = apiKey,
            delegate = xchange,
        )
    }
}
