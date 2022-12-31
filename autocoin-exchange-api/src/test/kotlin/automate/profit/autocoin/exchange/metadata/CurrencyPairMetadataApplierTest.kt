package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.api.exchange.metadata.CurrencyPairMetadata
import automate.profit.autocoin.api.exchange.metadata.CurrencyPairMetadataApplier
import automate.profit.autocoin.api.exchange.metadata.FeeRange
import automate.profit.autocoin.api.exchange.metadata.FeeRanges
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal


@ExtendWith(MockitoExtension::class)
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
    private val buyFeeMultiplier = BigDecimal.ZERO
    private val feeRanges = FeeRanges(
        makerFees = listOf(
            FeeRange(
                beginAmount = "0.05".toBigDecimal(),
                feeAmount = "0.02".toBigDecimal(),
            ),
            FeeRange(
                beginAmount = "0.25".toBigDecimal(),
                feeAmount = "0.01".toBigDecimal(),
            ),
        ),
        takerFees = listOf(
            FeeRange(
                beginAmount = "0.05".toBigDecimal(),
                feeAmount = "0.03".toBigDecimal(),
            ),
            FeeRange(
                beginAmount = "0.35".toBigDecimal(),
                feeAmount = "0.02".toBigDecimal(),
            ),
        ),
    )

    private val currencyPairMetadata = CurrencyPairMetadata(
        amountScale = amountScale,
        priceScale = 4,
        minimumAmount = minimumAmount,
        maximumAmount = maximumAmount,
        minimumOrderValue = minimumOrderValue,
        maximumPriceMultiplierDown = maximumPriceMultiplierUp,
        maximumPriceMultiplierUp = maximumPriceMultiplierDown,
        buyFeeMultiplier = buyFeeMultiplier,
        transactionFeeRanges = feeRanges,
    )

    @Test
    fun shouldSellAmountBeTheSameAsAmountWithoutFee() {
        // given
        val originalAmount = BigDecimal("45.123456")
        val originalPrice = BigDecimal("1.0")
        // when
        val amount = pairMetadataApplier.adjustAmount(originalAmount, originalPrice, currencyPairMetadata)
        val sellAmount = pairMetadataApplier.adjustSellAmount(originalAmount, originalPrice, currencyPairMetadata)
        // then
        assertThat(amount).isEqualTo(sellAmount)
    }

    @Test
    fun shouldBuyAmountBeTheSameAsAmountWithoutFee() {
        // given
        val originalAmount = BigDecimal("45.123456")
        val originalPrice = BigDecimal("1.0")
        // when
        val amount = pairMetadataApplier.adjustAmount(originalAmount, originalPrice, currencyPairMetadata)
        val sellAmount = pairMetadataApplier.adjustBuyAmount(originalAmount, originalPrice, currencyPairMetadata)
        // then
        assertThat(amount).isEqualTo(sellAmount)
    }

    @Test
    fun shouldDecreaseBuyAmountByFeeWhenNotEnoughCounterCurrencyAvailable() {
        // given
        val currencyPairMetadataWithBuyFee = currencyPairMetadata.copy(
            buyFeeMultiplier = BigDecimal("0.0025"),
        )
        val originalAmount = BigDecimal("3000.123456")
        val originalPrice = BigDecimal("0.015")
        val availableCounterCurrency = BigDecimal("45.00185184") // originalAmount * originalPrice
        // when
        val buyAmount = pairMetadataApplier.adjustBuyAmount(originalAmount, originalPrice, currencyPairMetadataWithBuyFee, availableCounterCurrency)
        // then
        assertThat(buyAmount.scale()).isEqualTo(amountScale)
        assertThat(buyAmount).isEqualTo(BigDecimal("2992.62314"))
    }

    @Test
    fun shouldReturnScaledDownAmount() {
        // given
        val originalAmount = BigDecimal("45.123456")
        val originalPrice = BigDecimal("1.0")
        // when
        val amount = pairMetadataApplier.adjustAmount(originalAmount, originalPrice, currencyPairMetadata)
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
        val amount = pairMetadataApplier.adjustAmount(originalAmount, originalPrice, currencyPairMetadata)
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
        val amount = pairMetadataApplier.adjustAmount(originalAmount, originalPrice, currencyPairMetadata)
        // then
        assertThat(amount).isEqualTo(BigDecimal.ZERO.setScale(amountScale))
    }

    @Test
    fun shouldReturnZeroAmountWhenBelowMinOrderValue() {
        // given
        val originalAmount = BigDecimal("0.999")
        val price = BigDecimal("0.001")
        // when
        val amount = pairMetadataApplier.adjustAmount(originalAmount, price, currencyPairMetadata.copy(minimumOrderValue = 0.005.toBigDecimal()))
        // then
        assertThat(amount).isEqualTo(BigDecimal.ZERO.setScale(amountScale))
    }

    @Test
    fun shouldReturnMaximumAmountWhenAboveMaximum() {
        // given
        val originalAmount = maximumAmount.plus(BigDecimal.ONE)
        val originalPrice = BigDecimal("1.0")
        // when
        val amount = pairMetadataApplier.adjustAmount(originalAmount, originalPrice, currencyPairMetadata)
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
            maximumPriceMultiplierUp = maximumPriceMultiplierUp,
            buyFeeMultiplier = buyFeeMultiplier,
            transactionFeeRanges = feeRanges,
        )
        // when
        val amount = pairMetadataApplier.adjustAmount(originalAmount, originalPrice, currencyPairMetadata)
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
            maximumPriceMultiplierUp = maximumPriceMultiplierUp,
            buyFeeMultiplier = buyFeeMultiplier,
            transactionFeeRanges = feeRanges,
        )
        // when
        val amount = pairMetadataApplier.adjustAmount(originalAmount, originalPrice, currencyPairMetadata)
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
