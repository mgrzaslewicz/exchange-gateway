package automate.profit.autocoin.exchange.metadata

import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.math.BigDecimal


@RunWith(MockitoJUnitRunner::class)
class CurrencyPairMetadataApplierTest {

    companion object : KLogging()

    private val pairMetadataApplier = CurrencyPairMetadataApplier()
    private val minimumAmount = 0.0001.toBigDecimal()
    private val maximumAmount = 10000.toBigDecimal()
    private val minimumOrderValue = 40.toBigDecimal()
    private val amountScale = 5
    private val priceScale = 4
    private val maximumPriceMultiplierDown = 0.1.toBigDecimal()
    private val maximumPriceMultiplierUp = BigDecimal.TEN

    private val currencyPairMetadata = CurrencyPairMetadata(
            amountScale = amountScale,
            priceScale = 4,
            minimumAmount = minimumAmount,
            maximumAmount = maximumAmount,
            minimumOrderValue = minimumOrderValue,
            maximumPriceMultiplierDown = maximumPriceMultiplierUp,
            maximumPriceMultiplierUp = maximumPriceMultiplierDown
    )

    @Test
    fun shouldReturnScaledDownAmount() {
        // given
        val originalAmount = BigDecimal("45.123456")
        val originalPrice = BigDecimal("1.0")
        // when
        val amount = pairMetadataApplier.applyAmountScaleAndLimits(originalAmount, originalPrice, currencyPairMetadata)
        // then
        assertThat(amount.scale()).isEqualTo(amountScale)
        assertThat(amount).isEqualTo(BigDecimal("45.12345"))
    }

    @Test
    fun shouldReturnScaledAmountWhenInputHasLowerScaleThanShouldHave() {
        // given
        val originalAmount = BigDecimal(45)
        val originalPrice = BigDecimal("1.0")
        // when
        val amount = pairMetadataApplier.applyAmountScaleAndLimits(originalAmount, originalPrice, currencyPairMetadata)
        // then
        assertThat(amount.scale()).isEqualTo(amountScale)
        assertThat(amount).isEqualTo(BigDecimal("45.00000"))
    }

    @Test
    fun shouldReturnZeroAmountWhenBelowMinimum() {
        // given
        val originalAmount = minimumAmount - BigDecimal("0.00001")
        val originalPrice = BigDecimal("1.0")
        // when
        val amount = pairMetadataApplier.applyAmountScaleAndLimits(originalAmount, originalPrice, currencyPairMetadata)
        // then
        assertThat(amount).isEqualTo(BigDecimal.ZERO.setScale(amountScale))
    }

    @Test
    fun shouldReturnZeroAmountWhenBelowMinOrderValue() {
        // given
        val originalAmount = BigDecimal("0.999")
        val price = BigDecimal("0.001")
        // when
        val amount = pairMetadataApplier.applyAmountScaleAndLimits(originalAmount, price, currencyPairMetadata.copy(minimumOrderValue = 0.005.toBigDecimal()))
        // then
        assertThat(amount).isEqualTo(BigDecimal.ZERO.setScale(amountScale))
    }

    @Test
    fun shouldReturnMaximumAmountWhenAboveMaximum() {
        // given
        val originalAmount = maximumAmount.plus(BigDecimal.ONE)
        val originalPrice = BigDecimal("1.0")
        // when
        val amount = pairMetadataApplier.applyAmountScaleAndLimits(originalAmount, originalPrice, currencyPairMetadata)
        // then
        assertThat(amount).isEqualTo(maximumAmount.setScale(amountScale))
    }

    @Test
    fun shouldReturnAmountWhenBelowMaximum() {
        // given
        val originalAmount = 1004.toBigDecimal()
        val originalPrice = BigDecimal("1.0")
        val currencyPairMetadata = CurrencyPairMetadata(
                amountScale = amountScale,
                priceScale = priceScale,
                minimumAmount = minimumAmount,
                maximumAmount = 5000.toBigDecimal(),
                minimumOrderValue = minimumOrderValue,
                maximumPriceMultiplierDown = maximumPriceMultiplierDown,
                maximumPriceMultiplierUp = maximumPriceMultiplierUp
        )
        // when
        val amount = pairMetadataApplier.applyAmountScaleAndLimits(originalAmount, originalPrice, currencyPairMetadata)
        // then
        assertThat(amount).isEqualTo(originalAmount.setScale(amountScale))
    }

    @Test
    fun shouldReturnAmountWhenAboveMinimum() {
        // given
        val originalAmount = minimumAmount.multiply(2.toBigDecimal())
        val originalPrice = BigDecimal("1.0")
        val currencyPairMetadata = CurrencyPairMetadata(
                amountScale = amountScale,
                priceScale = priceScale,
                minimumAmount = minimumAmount,
                maximumAmount = maximumAmount,
                minimumOrderValue = minimumAmount,
                maximumPriceMultiplierDown = maximumPriceMultiplierDown,
                maximumPriceMultiplierUp = maximumPriceMultiplierUp
        )
        // when
        val amount = pairMetadataApplier.applyAmountScaleAndLimits(originalAmount, originalPrice, currencyPairMetadata)
        // then
        assertThat(amount).isEqualTo(originalAmount.setScale(amountScale))
    }

    @Test
    fun shouldReturnPriceScaledAndRoundedDown() {
        // given
        val originalPrice = BigDecimal("1.0023456")
        // when
        val price = pairMetadataApplier.applyPriceScale(originalPrice, currencyPairMetadata)
        // then
        assertThat(price.scale()).isEqualTo(priceScale)
        assertThat(price).isEqualTo(BigDecimal("1.0023"))
    }

}
