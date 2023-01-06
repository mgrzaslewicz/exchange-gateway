package com.autocoin.exchangegateway.dto.exchange.metadata

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.api.exchange.metadata.ExchangeMetadata
import com.autocoin.exchangegateway.dto.SerializableToJson
import com.autocoin.exchangegateway.dto.appendMap
import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata as SpiExchangeMetadata

data class ExchangeMetadataDto(
    val exchange: String,
    val currencyPairMetadata: Map<String, CurrencyPairMetadataDto>,
    val currencyMetadata: Map<String, CurrencyMetadataDto>,
    val warnings: List<String>,
) : SerializableToJson {
    fun toExchangeMetadata() = ExchangeMetadata(
        exchange = ExchangeName(exchange),
        currencyPairMetadata = currencyPairMetadata.map { CurrencyPair.of(it.key) to it.value.toCurrencyPairMetadata() }.toMap(),
        currencyMetadata = currencyMetadata.mapValues { it.value.toCurrencyMetadata() },
        warnings = warnings,
    )

    override fun appendJson(builder: StringBuilder) = builder
        .append("{\"exchange\":\"")
        .append(exchange)
        .append("\",\"currencyPairMetadata\":")
        .appendMap(currencyPairMetadata)
        .append(",\"currencyMetadata\":")
        .appendMap(currencyMetadata)
        .append(",\"debugWarnings\":[")
        .apply {
            append(warnings.joinToString(",") { "\"$it\"" })
        }
        .append("]}")
}

fun SpiExchangeMetadata.toDto() = ExchangeMetadataDto(
    exchange = this.exchange.value,
    currencyMetadata = currencyMetadata.mapValues { it.value.toDto() },
    currencyPairMetadata = currencyPairMetadata.map { it.key.toStringWithSeparator() to it.value.toDto() }.toMap(),
    warnings = warnings,
)

