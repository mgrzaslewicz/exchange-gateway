package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
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
    private val minimum = 0.0001.toBigDecimal()
    private val maximum = 10000.toBigDecimal()
    private val priceScale = 5
    private val currencyPair = CurrencyPair("ETH", "BTC")
    private val currencyPairMetadata = CurrencyPairMetadata(
            scale = priceScale,
            minimumAmount = minimum,
            maximumAmount = maximum
    )

    @Test
    fun shouldReturnScaledDownAmount() {
        // given
        val originalAmount = BigDecimal("45.123456")
        val originalPrice = BigDecimal("1.0")
        val exchange = SupportedExchange.BITTREX
        // when
        val amount = pairMetadataApplier.applyAmountScaleAndLimits(originalAmount, originalPrice, currencyPair, currencyPairMetadata, exchange)
        // then
        assertThat(amount.scale()).isEqualTo(priceScale)
        assertThat(amount).isEqualTo(BigDecimal("45.12345"))
    }

    @Test
    fun shouldReturnScaledAmountWhenInputHasLowerScaleThanShouldHave() {
        // given
        val originalAmount = BigDecimal(45)
        val originalPrice = BigDecimal("1.0")
        val exchange = SupportedExchange.BITTREX
        // when
        val amount = pairMetadataApplier.applyAmountScaleAndLimits(originalAmount, originalPrice, currencyPair, currencyPairMetadata, exchange)
        // then
        assertThat(amount.scale()).isEqualTo(priceScale)
        assertThat(amount).isEqualTo(BigDecimal("45.00000"))
    }

    @Test
    fun shouldReturnZeroAmountWhenBelowMinimum() {
        // given
        val originalAmount = BigDecimal("0.00009")
        val originalPrice = BigDecimal("1.0")
        val exchange = SupportedExchange.BITTREX
        // when
        val amount = pairMetadataApplier.applyAmountScaleAndLimits(originalAmount, originalPrice, currencyPair, currencyPairMetadata, exchange)
        // then
        assertThat(amount).isEqualTo(BigDecimal.ZERO.setScale(priceScale))
    }

    /**
     * Based on https://support.binance.com/hc/en-us/articles/115000594711-Trading-Rule
     */
    @Test
    fun shouldReturnZeroAmountWhenBelowBtcMinNotionalOnBinance() {
        // given
        val originalAmount = BigDecimal("0.999")
        val originalPrice = BigDecimal("0.001")
        val exchange = SupportedExchange.BINANCE
        // when
        val amount = pairMetadataApplier.applyAmountScaleAndLimits(originalAmount, originalPrice, currencyPair, currencyPairMetadata, exchange)
        // then
        assertThat(amount).isEqualTo(BigDecimal.ZERO.setScale(priceScale))
    }

    @Test
    fun shouldReturnMaximumAmountWhenAboveMaximum() {
        // given
        val originalAmount = maximum.plus(BigDecimal.ONE)
        val originalPrice = BigDecimal("1.0")
        val exchange = SupportedExchange.BITTREX
        // when
        val amount = pairMetadataApplier.applyAmountScaleAndLimits(originalAmount, originalPrice, currencyPair, currencyPairMetadata, exchange)
        // then
        assertThat(amount).isEqualTo(maximum.setScale(priceScale))
    }

    @Test
    fun shouldReturnAmountWhenNoMaximumSet() {
        // given
        val originalAmount = BigDecimal.valueOf(Long.MAX_VALUE)
        val originalPrice = BigDecimal("1.0")
        val currencyPairMetadata = CurrencyPairMetadata(
                scale = priceScale,
                minimumAmount = minimum,
                maximumAmount = null
        )
        val exchange = SupportedExchange.BITTREX
        // when
        val amount = pairMetadataApplier.applyAmountScaleAndLimits(originalAmount, originalPrice, currencyPair, currencyPairMetadata, exchange)
        // then
        assertThat(amount).isEqualTo(originalAmount.setScale(priceScale))
    }

    @Test
    fun shouldReturnAmountWhenNoMinimumSet() {
        // given
        val originalAmount = BigDecimal("0.00000001")
        val originalPrice = BigDecimal("1.0")
        val currencyPairMetadata = CurrencyPairMetadata(
                scale = priceScale,
                minimumAmount = null,
                maximumAmount = maximum
        )
        val exchange = SupportedExchange.BITTREX
        // when
        val amount = pairMetadataApplier.applyAmountScaleAndLimits(originalAmount, originalPrice, currencyPair, currencyPairMetadata, exchange)
        // then
        assertThat(amount).isEqualTo(BigDecimal("0.00000"))
    }

}
