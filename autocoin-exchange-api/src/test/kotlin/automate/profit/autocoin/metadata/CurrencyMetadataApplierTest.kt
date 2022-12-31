package automate.profit.autocoin.metadata

import automate.profit.autocoin.exchange.metadata.CurrencyMetadata
import automate.profit.autocoin.exchange.metadata.CurrencyMetadataApplier
import automate.profit.autocoin.exchange.metadata.CurrencyPairMetadata
import mu.KLogging
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.math.BigDecimal

@RunWith(MockitoJUnitRunner::class)
class CurrencyMetadataApplierTest {

    companion object : KLogging()

    private val tested = CurrencyMetadataApplier()
    private val minimum = 5.toBigDecimal()
    private val maximum = 50.toBigDecimal()
    private val priceScale = 5
    private val amountScale = 4

    @Test
    fun shouldReturnScaledAmount() {
        // when
        val amount = tested.applyAmountScaleAndLimits(
                BigDecimal("45.00333452"),
                CurrencyMetadata(amountScale),
                CurrencyPairMetadata(
                        scale = priceScale,
                        minimumAmount = minimum,
                        maximumAmount = maximum
                )
        )
        // then
        assertThat(amount.scale()).isEqualTo(amountScale)
        assertThat(amount).isEqualTo(BigDecimal("45.0033"))
    }

    @Test
    fun shouldReturnZeroAmountWhenBelowMinimum() {
        // given
        val belowMinimum = minimum.minus(BigDecimal("1.6943267"))
        // when
        val amount = tested.applyAmountScaleAndLimits(
                belowMinimum,
                CurrencyMetadata(amountScale),
                CurrencyPairMetadata(
                        scale = priceScale,
                        minimumAmount = minimum,
                        maximumAmount = maximum
                )
        )
        // then
        assertThat(amount).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun shouldReturnMaximumAmountWhenAboveMaximum() {
        // given
        val aboveMaximum = maximum.plus(BigDecimal("11.4567922394"))
        // when
        val amount = tested.applyAmountScaleAndLimits(
                aboveMaximum,
                CurrencyMetadata(amountScale),
                CurrencyPairMetadata(
                        scale = priceScale,
                        minimumAmount = minimum,
                        maximumAmount = maximum
                )
        )
        // then
        assertThat(amount).isEqualTo(BigDecimal("50.0000"))
    }

    @Test
    fun shouldReturnAmountWhenNoMaximumSet() {
        // when
        val amount = tested.applyAmountScaleAndLimits(
                BigDecimal("9999611.4567922394"),
                CurrencyMetadata(amountScale),
                CurrencyPairMetadata(
                        scale = priceScale,
                        minimumAmount = minimum,
                        maximumAmount = Long.MAX_VALUE.toBigDecimal()
                )
        )
        // then
        assertThat(amount).isEqualTo(BigDecimal("9999611.4567"))
    }

    @Test
    fun shouldReturnAmountWhenNoMinimumSet() {
        // when
        val amount = tested.applyAmountScaleAndLimits(
                0.000000001.toBigDecimal(),
                CurrencyMetadata(amountScale),
                CurrencyPairMetadata(
                        scale = priceScale,
                        minimumAmount = minimum,
                        maximumAmount = maximum
                )
        )
        // then
        assertThat(amount).isEqualTo(0.toBigDecimal())
    }

}
