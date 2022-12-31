package automate.profit.autocoin.exchange.metadata
import mu.KLogging
import java.math.BigDecimal
import java.math.BigDecimal.ROUND_DOWN
import java.math.BigDecimal.ZERO

class CurrencyMetadataApplier {
    companion object : KLogging()

    private fun BigDecimal?.orMax() = this ?: BigDecimal.valueOf(Long.MAX_VALUE)
    private fun BigDecimal?.orMin() = this ?: 0.00000001.toBigDecimal()

    fun applyAmountScaleAndLimits(amount: BigDecimal, currencyMetadata: CurrencyMetadata, currencyPairMetadata: CurrencyPairMetadata): BigDecimal {
        val result = when {
            amount < currencyPairMetadata.minimumAmount.orMin() -> ZERO.also { logger.info("amount $amount below minimum ${currencyPairMetadata.minimumAmount}, cutting down to 0") }
            amount > currencyPairMetadata.maximumAmount.orMax() -> currencyPairMetadata.maximumAmount.also { logger.info("amount $amount above maximum ${currencyPairMetadata.maximumAmount}, cutting down to ${currencyPairMetadata.maximumAmount}") }
            else -> amount
        }
        return if (result != ZERO) applyScale(result, currencyMetadata.scale)
        else ZERO
    }

    private fun applyScale(number: BigDecimal, scale: Int) = number.stripTrailingZeros().setScale(scale, ROUND_DOWN)
            .also { logger.debug { "applyScale($number, $scale) = $it" } }

    fun applyPriceScale(price: BigDecimal, currencyPairMetadata: CurrencyPairMetadata): BigDecimal {
        return applyScale(price, currencyPairMetadata.scale)
    }

}
