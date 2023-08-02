package com.autocoin.exchangegateway.spi.exchange.metadata.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata

interface AuthorizedMetadataServiceGateway {
    fun getMetadata(
        exchange: Exchange,
    ): ExchangeMetadata

    fun getAllExchangesMetadata(): Map<Exchange, ExchangeMetadata>
}
