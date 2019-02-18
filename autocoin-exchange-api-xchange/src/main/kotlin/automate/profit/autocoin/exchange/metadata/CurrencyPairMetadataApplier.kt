package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import mu.KLogging
import org.knowm.xchange.dto.meta.CurrencyPairMetaData
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
    fun getMinimumAmountForPrice(price: BigDecimal, ccyPair: CurrencyPair, metadata: CurrencyPairMetaData, exchange: SupportedExchange): BigDecimal {
        var minAmount = metadata.minimumAmount?.stripTrailingZeros() ?: ZERO
        if (exchange == SupportedExchange.BINANCE) {
            // So called MIN_NOTIONAL condition. This will work only for BTC (0.001) based currency pairs. Would need to adjust to other ccy pairs.
            val minNotionalAmount = BigDecimal(getMinNotionalValue(ccyPair)).divide(price, minAmount.scale(), RoundingMode.UP)
            if (minNotionalAmount > minAmount) {
                minAmount = minNotionalAmount
            }
        }
        return minAmount
    }

    fun applyAmountScaleAndLimits(amount: BigDecimal, price: BigDecimal, currencyPair: CurrencyPair, currencyPairMetadata: CurrencyPairMetaData, exchange: SupportedExchange): BigDecimal {
        val resultAmount = when {
            amount < getMinimumAmountForPrice(price, currencyPair, currencyPairMetadata, exchange).orMin() -> ZERO.also { logger.info("amount $amount below minimum ${currencyPairMetadata.minimumAmount}, cutting down to 0") }
            amount > currencyPairMetadata.maximumAmount.orMax() -> currencyPairMetadata.maximumAmount.also { logger.info("amount $amount above maximum ${currencyPairMetadata.maximumAmount}, cutting down to ${currencyPairMetadata.maximumAmount}") }
            else -> amount
        }.stripTrailingZeros()
        val scale = when {
            resultAmount.scale() > currencyPairMetadata.minimumAmount.orMin().stripTrailingZeros().scale() -> currencyPairMetadata.minimumAmount.stripTrailingZeros().scale()
            resultAmount.scale() < 0 -> 0 // This one is not a strict requirement, just to avoid numbers like 1e4 instead of 10000 during debugging/logging/testing
            else -> resultAmount.scale()
        }
        return applyScale(resultAmount.stripTrailingZeros(), scale)
    }

    fun applyPriceScale(price: BigDecimal, currencyPairMetadata: CurrencyPairMetaData): BigDecimal {
        return applyScale(price, currencyPairMetadata.priceScale)
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
