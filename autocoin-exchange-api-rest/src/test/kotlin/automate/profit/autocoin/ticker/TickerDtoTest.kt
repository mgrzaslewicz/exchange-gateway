package automate.profit.autocoin.ticker

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.ticker.Ticker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TickerDtoTest {
    @Test
    fun shouldSerializeTicker() {
        // given
        val currencyPair = CurrencyPair.of("STORJ/BTC")
        val ticker = Ticker(
            currencyPair = currencyPair,
            ask = BigDecimal.ZERO,
            bid = BigDecimal.ONE,
            baseCurrency24hVolume = BigDecimal.TEN,
            counterCurrency24hVolume = BigDecimal.TEN,
            receivedAtMillis = System.currentTimeMillis(),
            exchangeTimestampMillis = null,
        )
        // when
        val dto = ticker.toDto(SupportedExchange.BITTREX)
        // then
        assertThat(dto.exchange).isEqualTo("bittrex")
        assertThat(dto.currencyPair).isEqualTo("STORJ/BTC")
    }
}
