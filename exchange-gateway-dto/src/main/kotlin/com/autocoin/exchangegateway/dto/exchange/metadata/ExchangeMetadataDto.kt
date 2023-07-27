package com.autocoin.exchangegateway.dto.exchange.metadata

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.api.exchange.metadata.ExchangeMetadata
import com.autocoin.exchangegateway.spi.exchange.ExchangeProvider
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata as SpiExchangeMetadata

data class ExchangeMetadataDto(
    val exchange: String,
    val currencyPairMetadata: Map<String, CurrencyPairMetadataDto>,
    val currencyMetadata: Map<String, CurrencyMetadataDto>,
    val warnings: List<String>,
) {
    fun toExchangeMetadata(exchangeProvider: ExchangeProvider) = ExchangeMetadata(
        exchange = exchangeProvider.getExchange(exchange),
        currencyPairMetadata = currencyPairMetadata.map { CurrencyPair.of(it.key) to it.value.toCurrencyPairMetadata() }
            .toMap(),
        currencyMetadata = currencyMetadata.mapValues { it.value.toCurrencyMetadata() },
        warnings = warnings,
    )

}

fun SpiExchangeMetadata.toDto() = ExchangeMetadataDto(
    exchange = this.exchange.exchangeName,
    currencyMetadata = currencyMetadata.mapValues { it.value.toDto() },
    currencyPairMetadata = currencyPairMetadata.map { it.key.toStringWithSeparator() to it.value.toDto() }.toMap(),
    warnings = warnings,
)

