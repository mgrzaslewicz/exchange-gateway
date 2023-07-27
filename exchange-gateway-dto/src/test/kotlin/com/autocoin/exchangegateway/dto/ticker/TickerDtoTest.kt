package com.autocoin.exchangegateway.dto.ticker

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.api.exchange.ticker.Ticker
import com.autocoin.exchangegateway.spi.exchange.Exchange
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TickerDtoTest {
    private val exchange = object : Exchange {
        override val exchangeName = "exchange"
    }

    private val currencyPair = CurrencyPair.of("ABCDE/FGHIJ")
    private val ticker = Ticker(
        exchange = exchange,
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
        val tickerFromDto = dto.toTicker { exchange }
        // then
        assertThat(ticker).isEqualTo(tickerFromDto)
    }

}
