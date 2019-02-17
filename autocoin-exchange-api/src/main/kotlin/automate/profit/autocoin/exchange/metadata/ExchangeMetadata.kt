package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.currency.CurrencyPair
import java.math.BigDecimal

data class CurrencyPairMetadata(
        val scale: Int,
        val minimumAmount: BigDecimal
)

data class CurrencyMetadata(
        val scale: Int
)

data class ExchangeMetadata(
        val currencyPairMetadata: Map<CurrencyPair, CurrencyPairMetadata>,
        val currencyMetadata: Map<String, CurrencyMetadata>
) {
    fun currencies() = currencyMetadata.keys

    fun currencyPairs() = currencyPairMetadata.keys
}
