package automate.profit.autocoin.api.exchange.metadata

import mu.KLogging
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.BigDecimal.valueOf
import java.math.RoundingMode

class CurrencyPairMetadataApplier {
    companion object : KLogging()

    private val MAX_AMOUNT = valueOf(Long.MAX_VALUE)

    /**
     * Returns lowest possible amount for currency pair and price allowed on a given exchange
     */
    private fun getMinimumAmountForPrice(price: BigDecimal, metadata: CurrencyPairMetadata): BigDecimal {
        var minAmount = metadata.minimumAmount.stripTrailingZeros()
        val minAmountForMinOrderValue = metadata.minimumOrderValue.divide(price, minAmount.scale(), RoundingMode.UP)

        if (minAmount < minAmountForMinOrderValue) {
            logger.info { "Minimum order value is ${metadata.minimumOrderValue}, increasing minimum amount to $minAmountForMinOrderValue" }
            minAmount = minAmountForMinOrderValue
        }
        return minAmount
    }

    fun adjustSellAmount(baseCurrencyAmount: BigDecimal, price: BigDecimal, currencyPairMetadata: CurrencyPairMetadata): BigDecimal {
        return adjustAmount(baseCurrencyAmount, price, currencyPairMetadata)
    }


    fun adjustBuyAmount(
        baseCurrencyAmount: BigDecimal,
        price: BigDecimal,
        currencyPairMetadata: CurrencyPairMetadata,
        availableCounterCurrency: BigDecimal = MAX_AMOUNT
    ): BigDecimal {
        val availableCounterCurrencyMinusFee = availableCounterCurrency - availableCounterCurrency * currencyPairMetadata.buyFeeMultiplier
        val currencyToBuyValue = baseCurrencyAmount * price
        val baseCurrencyAmountMinusFee = when {
            currencyToBuyValue > availableCounterCurrencyMinusFee -> {
                availableCounterCurrencyMinusFee.divide(price, RoundingMode.HALF_UP)
            }
            else -> baseCurrencyAmount
        }
        if (baseCurrencyAmount > baseCurrencyAmountMinusFee) {
            logger.info { "Buy amount $baseCurrencyAmount decreased to $baseCurrencyAmountMinusFee because of the buy fee" }
        }
        return adjustAmount(baseCurrencyAmountMinusFee, price, currencyPairMetadata)
    }

    internal fun adjustAmount(originalAmount: BigDecimal, price: BigDecimal, currencyPairMetadata: CurrencyPairMetadata): BigDecimal {
        val minAmount = getMinimumAmountForPrice(price, currencyPairMetadata)
        val resultAmount = when {
            originalAmount < minAmount -> ZERO.also { logger.warn("Amount $originalAmount below minimum $minAmount. Cutting it down to 0.") }
            originalAmount > currencyPairMetadata.maximumAmount -> currencyPairMetadata.maximumAmount.also { logger.info("Amount $originalAmount above maximum ${currencyPairMetadata.maximumAmount}. Cutting down to ${currencyPairMetadata.maximumAmount}.") }
            else -> originalAmount
        }
        return applyScale(resultAmount, currencyPairMetadata.amountScale)
    }

    fun applyPriceScale(price: BigDecimal, currencyPairMetadata: CurrencyPairMetadata): BigDecimal {
        return applyScale(price, currencyPairMetadata.priceScale)
    }

    private fun applyScale(value: BigDecimal, scale: Int) = value.setScale(scale, RoundingMode.DOWN).also { logger.debug { "applyScale($value, $scale) = $it" } }

}
