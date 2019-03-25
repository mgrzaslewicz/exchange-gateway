package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.currency.CurrencyPair

data class CurrencyPairMetadataDto(
        val amountScale: Int,
        val priceScale: Int,
        val minimumAmount: Double,
        val maximumAmount: Double,
        val minimumOrderValue: Double,
        val maximumPriceMultiplierUp: Double,
        val maximumPriceMultiplierDown: Double
) {
    fun toCurrencyPairMetadata() = CurrencyPairMetadata(
            amountScale = amountScale,
            priceScale = priceScale,
            minimumAmount = minimumAmount.toBigDecimal(),
            maximumAmount = maximumAmount.toBigDecimal(),
            minimumOrderValue = minimumOrderValue.toBigDecimal(),
            maximumPriceMultiplierUp = maximumPriceMultiplierUp.toBigDecimal(),
            maximumPriceMultiplierDown = maximumPriceMultiplierDown.toBigDecimal()
    )
}

data class CurrencyMetadataDto(
        val scale: Int
) {
    fun toCurrencyMetadata() = CurrencyMetadata(
            scale = scale
    )
}

data class ExchangeMetadataDto(
        val currencyPairMetadata: Map<CurrencyPair, CurrencyPairMetadataDto>,
        val currencyMetadata: Map<String, CurrencyMetadataDto>
) {
    fun toExchangeMetadata() = ExchangeMetadata(
            currencyPairMetadata = currencyPairMetadata.mapValues { it.value.toCurrencyPairMetadata() },
            currencyMetadata = currencyMetadata.mapValues { it.value.toCurrencyMetadata() }
    )
}

fun ExchangeMetadata.toDto() = ExchangeMetadataDto(
        currencyMetadata = currencyMetadata.mapValues { it.value.toDto() },
        currencyPairMetadata = currencyPairMetadata.mapValues { it.value.toDto() }
)


