package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.SerializableToJson
import automate.profit.autocoin.api.exchange.currency.CurrencyPair
import automate.profit.autocoin.api.exchange.metadata.ExchangeMetadata
import automate.profit.autocoin.appendMap
import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.metadata.ExchangeMetadata as SpiExchangeMetadata

data class ExchangeMetadataDto(
    val exchange: String,
    val currencyPairMetadata: Map<String, CurrencyPairMetadataDto>,
    val currencyMetadata: Map<String, CurrencyMetadataDto>,
    val debugWarnings: List<String>,
) : SerializableToJson {
    fun toExchangeMetadata(): SpiExchangeMetadata = ExchangeMetadata(
        exchange = ExchangeName(exchange),
        currencyPairMetadata = currencyPairMetadata.map { CurrencyPair.of(it.key) to it.value.toCurrencyPairMetadata() }.toMap(),
        currencyMetadata = currencyMetadata.mapValues { it.value.toCurrencyMetadata() },
        debugWarnings = debugWarnings,
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
            append(debugWarnings.joinToString(",") { "\"$it\"" })
        }
        .append("]}")
}

fun ExchangeMetadata.toDto() = ExchangeMetadataDto(
    exchange = this.exchange.value,
    currencyMetadata = currencyMetadata.mapValues { it.value.toDto() },
    currencyPairMetadata = currencyPairMetadata.map { it.key.toStringWithSeparator('/') to it.value.toDto() }.toMap(),
    debugWarnings = debugWarnings,
)

