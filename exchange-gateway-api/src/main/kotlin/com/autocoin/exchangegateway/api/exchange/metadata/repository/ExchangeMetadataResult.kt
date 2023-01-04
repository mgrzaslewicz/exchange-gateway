package com.autocoin.exchangegateway.api.exchange.metadata.repository

import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata

data class ExchangeMetadataResult(
    val exchangeMetadata: ExchangeMetadata? = null,
    val exception: Exception? = null,
) {
    fun hasMetadata() = exchangeMetadata != null
    fun hasException() = exception != null
}
