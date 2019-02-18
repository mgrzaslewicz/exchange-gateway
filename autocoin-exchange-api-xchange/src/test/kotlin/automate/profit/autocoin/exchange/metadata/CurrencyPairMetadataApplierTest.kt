package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.knowm.xchange.dto.meta.CurrencyPairMetaData
import org.mockito.junit.MockitoJUnitRunner
import java.math.BigDecimal


// TODO move tests calculating amount order here from StrategyExecutorIntegrationTest or add separate test for both amount and price calculation
@RunWith(MockitoJUnitRunner::class)
class CurrencyPairMetadataApplierTest {

    companion object : KLogging()

    private val pairMetadataApplier = CurrencyPairMetadataApplier()
    private val minimum = 0.0001.toBigDecimal()
    private val maximum = 10000.toBigDecimal()
    private val priceScale = 5
    private val currencyPair = CurrencyPair("ETH", "BTC")
    private val currencyPairMetadata = CurrencyPairMetaData(null, minimum, maximum, priceScale, null)

    @Test
    fun shouldReturnScaledAmount() {
        // given
        val originalAmount = BigDecimal("45.123456")
        val originalPrice = BigDecimal("1.0")
        val exchange = SupportedExchange.BITTREX
        // when
        val amount = pairMetadataApplier.applyAmountScaleAndLimits(originalAmount, originalPrice, currencyPair, currencyPairMetadata, exchange)
        // then
        assertThat(amount.scale()).isEqualTo(4)
        assertThat(amount).isEqualTo(BigDecimal("45.1234"))
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
        assertThat(amount).isEqualTo(BigDecimal.ZERO)
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
        assertThat(amount).isEqualTo(BigDecimal.ZERO)
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
        assertThat(amount).isEqualTo(maximum)
    }

    @Test
    fun shouldReturnAmountWhenNoMaximumSet() {
        // given
        val originalAmount = BigDecimal.valueOf(Long.MAX_VALUE)
        val originalPrice = BigDecimal("1.0")
        val currencyPairMetadata = CurrencyPairMetaData(null, minimum, null, priceScale, null)
        val exchange = SupportedExchange.BITTREX
        // when
        val amount = pairMetadataApplier.applyAmountScaleAndLimits(originalAmount, originalPrice, currencyPair, currencyPairMetadata, exchange)
        // then
        assertThat(amount).isEqualTo(originalAmount)
    }

    @Test
    fun shouldReturnAmountWhenNoMinimumSet() {
        // given
        val originalAmount = BigDecimal("0.00000001")
        val originalPrice = BigDecimal("1.0")
        val currencyPairMetadata = CurrencyPairMetaData(null, null, maximum, priceScale, null)
        val exchange = SupportedExchange.BITTREX
        // when
        val amount = pairMetadataApplier.applyAmountScaleAndLimits(originalAmount, originalPrice, currencyPair, currencyPairMetadata, exchange)
        // then
        assertThat(amount).isEqualTo(originalAmount)
    }

}
