package automate.profit.autocoin.ticker

import automate.profit.autocoin.TestObjectMapper
import automate.profit.autocoin.api.exchange.currency.CurrencyPair
import automate.profit.autocoin.api.exchange.ticker.Ticker
import automate.profit.autocoin.spi.exchange.ExchangeName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.system.measureTimeMillis

class TickerDtoTest {
    private val currencyPair = CurrencyPair.of("ABCDE/FGHIJ")
    private val ticker = Ticker(
        exchangeName = ExchangeName("exchange1"),
        currencyPair = currencyPair,
        ask = 0.15.toBigDecimal(),
        bid = BigDecimal.ONE,
        baseCurrency24hVolume = BigDecimal.TEN,
        counterCurrency24hVolume = BigDecimal.TEN,
        receivedAtMillis = 123L,
        exchangeTimestampMillis = null,
    )
    private val dto = ticker.toDto()

    @Test
    fun shouldConvertToDtoAndBack() {
        // when
        val tickerFromDto = dto.toTicker()
        // then
        assertThat(ticker).isEqualTo(tickerFromDto)
    }

    @Test
    fun shouldSerializeToJson() {
        // when
        val objectMapper = TestObjectMapper().createObjectMapper()
        val json = dto.toJson()
        // then
        assertThat(json).isEqualTo(objectMapper.writeValueAsString(dto))
    }

    /**
     * Just a quick test to show that it makes sense not to use reflection for serialization.
     * It's 20-40% faster than jackson library.
     */
    @Test
    fun shouldStringBuilderSerializationBeQuickerThanJackson() {
        // given
        val objectMapper = TestObjectMapper().createObjectMapper()
        // when
        val numberOfMeasurements = 1_000_000
        val noLibrarySerializationDurationMillis = measureTimeMillis {
            for (i in 1..numberOfMeasurements) {
                dto.toJson()
            }
        }
        val jacksonSerializationDurationMillis = measureTimeMillis {
            for (i in 1..numberOfMeasurements) {
                objectMapper.writeValueAsString(dto)
            }
        }
        // then
        assertThat(noLibrarySerializationDurationMillis).isLessThan(jacksonSerializationDurationMillis)
        println("No library serialization took $noLibrarySerializationDurationMillis ms, Jackson serialization took $jacksonSerializationDurationMillis ms")
    }

}
