package com.autocoin.exchangegateway.spi.exchange.metadata.service

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata

interface MetadataService {
    val exchangeName: ExchangeName
    fun getMetadata(): ExchangeMetadata
    fun refreshMetadata()
}
