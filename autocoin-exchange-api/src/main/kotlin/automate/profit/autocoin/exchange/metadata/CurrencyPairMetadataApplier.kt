package automate.profit.autocoin.exchange.metadata

import mu.KLogging
import java.math.BigDecimal
import java.math.BigDecimal.*
import java.math.RoundingMode

class CurrencyPairMetadataApplier {
    companion object : KLogging()


    /**
     * Returns lowest possible amount for currency pair and price allowed on a given exchange
     */
    private fun getMinimumAmountForPrice(price: BigDecimal, metadata: CurrencyPairMetadata): BigDecimal {
        var minAmount = metadata.minimumAmount.stripTrailingZeros()
        val minAmountForMinOrderValue = metadata.minimumOrderValue.divide(price, minAmount.scale(), RoundingMode.UP)

        if (minAmount < minAmountForMinOrderValue) {
            minAmount = minAmountForMinOrderValue
        }
        return minAmount
    }

    fun applyAmountScaleAndLimits(amount: BigDecimal, price: BigDecimal, currencyPairMetadata: CurrencyPairMetadata): BigDecimal {
        val minAmount = getMinimumAmountForPrice(price, currencyPairMetadata)
        val resultAmount = when {
            amount < minAmount -> ZERO.also { logger.warn("Amount $amount below minimum $minAmount. Cutting it down to 0.") }
            amount > currencyPairMetadata.maximumAmount -> currencyPairMetadata.maximumAmount.also { logger.info("Amount $amount above maximum ${currencyPairMetadata.maximumAmount}. Cutting down to ${currencyPairMetadata.maximumAmount}.") }
            else -> amount
        }
        return applyScale(resultAmount, currencyPairMetadata.amountScale)
    }

    fun applyPriceScale(price: BigDecimal, currencyPairMetadata: CurrencyPairMetadata): BigDecimal {
        return applyScale(price, currencyPairMetadata.priceScale)
    }

    private fun applyScale(value: BigDecimal, scale: Int) = value.setScale(scale, ROUND_DOWN).also { logger.debug { "applyScale($value, $scale) = $it" } }

}
