package com.autocoin.exchangegateway.spi.exchange.metadata.service.authorized

import com.autocoin.exchangegateway.spi.exchange.AuthorizedService
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata

interface AuthorizedMetadataService<T> : AuthorizedService<T> {
    fun getMetadata(): ExchangeMetadata
}
