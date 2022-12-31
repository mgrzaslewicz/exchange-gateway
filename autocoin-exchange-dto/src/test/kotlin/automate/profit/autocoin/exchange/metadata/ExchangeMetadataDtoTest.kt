package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.TestObjectMapper
import automate.profit.autocoin.api.exchange.currency.CurrencyPair
import automate.profit.autocoin.api.exchange.metadata.CurrencyPairMetadata
import automate.profit.autocoin.api.exchange.metadata.ExchangeMetadata
import automate.profit.autocoin.api.exchange.metadata.FeeRange
import automate.profit.autocoin.api.exchange.metadata.FeeRanges
import automate.profit.autocoin.spi.exchange.ExchangeName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ExchangeMetadataDtoTest {
    private val exchangeMetadata = ExchangeMetadata(
        exchange = ExchangeName("exchange1"),
        currencyPairMetadata = mapOf(
            CurrencyPair.of("A/B") to CurrencyPairMetadata(
                amountScale = 2,
                priceScale = 3,
                minimumAmount = 0.01.toBigDecimal(),
                maximumAmount = 1001.0.toBigDecimal(),
                minimumOrderValue = 0.02.toBigDecimal(),
                maximumPriceMultiplierUp = 1.1.toBigDecimal(),
                maximumPriceMultiplierDown = 0.2.toBigDecimal(),
                buyFeeMultiplier = 0.03.toBigDecimal(),
                transactionFeeRanges = FeeRanges(
                    makerFees = listOf(
                        FeeRange(
                            beginAmount = 0.01.toBigDecimal(),
                            feeAmount = 0.027.toBigDecimal(),
                            feeRatio = null,
                        ),
                    ),
                    takerFees = listOf(
                        FeeRange(
                            beginAmount = 0.012.toBigDecimal(),
                            feeAmount = null,
                            feeRatio = 0.02222.toBigDecimal(),
                        ),
                    ),
                ),
            ),
        ),
        currencyMetadata = mapOf(

        ),
        debugWarnings = listOf(
            "Requires API keys to get ticker",
        ),
    )
    private val dto = exchangeMetadata.toDto()

    @Test
    fun shouldConvertToDtoAndBack() {
        // when
        val fromDto = dto.toExchangeMetadata()
        // then
        assertThat(fromDto).isEqualTo(exchangeMetadata)
    }

    @Test
    fun shouldSerializeToJson() {
        // given
        val objectMapper = TestObjectMapper().createObjectMapper()
        // when
        val json = dto.toJson()
        // then
        assertThat(json).isEqualTo(objectMapper.writeValueAsString(dto))
    }

}
