package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.currency.CurrencyPair
import java.math.BigDecimal

data class CurrencyPairMetadata(
        val amountScale: Int,
        val priceScale: Int,
        val minimumAmount: BigDecimal,
        val maximumAmount: BigDecimal,
        val minimumOrderValue: BigDecimal,
        val maximumPriceMultiplierUp: BigDecimal,
        val maximumPriceMultiplierDown: BigDecimal,
        /**
         * Buy fee that exchange is going to add to buy amount at exchange side.
         * That means the final amount of counter currency needed would be bigger if fee not applied before creating order
         */
        val buyFeeMultiplier: BigDecimal
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
