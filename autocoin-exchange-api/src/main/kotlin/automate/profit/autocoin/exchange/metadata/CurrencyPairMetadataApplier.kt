package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import mu.KLogging
import java.math.BigDecimal
import java.math.BigDecimal.ROUND_DOWN
import java.math.BigDecimal.ZERO
import java.math.RoundingMode

class CurrencyPairMetadataApplier {
    companion object : KLogging()

    private fun BigDecimal?.orMax() = this ?: BigDecimal.valueOf(Long.MAX_VALUE)
    private fun BigDecimal?.orMin() = this ?: 0.00000001.toBigDecimal()

    /**
     * Returns lowest possible amount for currency pair and price allowed on a given exchange
     */
    fun getMinimumAmountForPrice(price: BigDecimal, ccyPair: CurrencyPair, metadata: CurrencyPairMetadata, exchange: SupportedExchange): BigDecimal {
        var minAmount = metadata.minimumAmount?.stripTrailingZeros() ?: ZERO

        // This can happen e.g. on Binance where you can have currencies in wallet which are not traded yet and are reported with price 0e-8
        if (price == ZERO)
            minAmount = ZERO
        else if (exchange == SupportedExchange.BINANCE) {
            // So called MIN_NOTIONAL condition. This will work only for BTC (0.001) based currency pairs. Would need to adjust to other ccy pairs.
            val minNotionalAmount = BigDecimal(getMinNotionalValue(ccyPair)).divide(price, minAmount.scale(), RoundingMode.UP)
            if (minNotionalAmount > minAmount) {
                minAmount = minNotionalAmount
            }
        }
        return minAmount
    }

    fun applyAmountScaleAndLimits(amount: BigDecimal, price: BigDecimal, currencyPair: CurrencyPair, currencyPairMetadata: CurrencyPairMetadata, exchange: SupportedExchange): BigDecimal {
        val minAmount = getMinimumAmountForPrice(price, currencyPair, currencyPairMetadata, exchange).orMin()
        val resultAmount = when {
            amount < minAmount -> ZERO.also { logger.warn("Amount $amount below minimum $minAmount. Cutting it down to 0.") }
            amount > currencyPairMetadata.maximumAmount.orMax() -> currencyPairMetadata.maximumAmount.orMax().also { logger.info("Amount $amount above maximum ${currencyPairMetadata.maximumAmount}. Cutting down to ${currencyPairMetadata.maximumAmount}.") }
            else -> amount
        }.stripTrailingZeros()
        val scale = when {
            resultAmount.scale() > currencyPairMetadata.minimumAmount.orMin().stripTrailingZeros().scale() -> currencyPairMetadata.minimumAmount.orMin().stripTrailingZeros().scale()
            resultAmount.scale() < 0 -> 0 // This one is not a strict requirement, just to avoid numbers like 1e4 instead of 10000 during debugging/logging/testing
            else -> resultAmount.scale()
        }
        return applyScale(resultAmount.stripTrailingZeros(), scale)
    }

    fun applyPriceScale(price: BigDecimal, currencyPairMetadata: CurrencyPairMetadata): BigDecimal {
        return applyScale(price, currencyPairMetadata.scale)
    }

    private fun applyScale(value: BigDecimal, scale: Int) = value.setScale(scale, ROUND_DOWN).also { logger.debug("applyScale($value, $scale) = $it") }

    // Based on https://support.binance.com/hc/en-us/articles/115000594711-Trading-Rule
    private fun getMinNotionalValue(ccyPair: CurrencyPair): Double {
        return when {
            ccyPair.contains("USDT") -> 10.0
            ccyPair.contains("PAX") -> 10.0
            ccyPair.contains("TUSD") -> 10.0
            ccyPair.contains("USDC") -> 10.0
            ccyPair.contains("USDS") -> 10.0
            ccyPair.contains("BTC") -> 0.001
            ccyPair.contains("ETH") -> 0.01
            ccyPair.contains("BNB") -> 1.0
            else -> throw IllegalArgumentException("Unable to identify MIN_NOTIONAL. Please fix the mapping.") // Maybe should not throw exception but just log error and return 0?
        }
    }

}
