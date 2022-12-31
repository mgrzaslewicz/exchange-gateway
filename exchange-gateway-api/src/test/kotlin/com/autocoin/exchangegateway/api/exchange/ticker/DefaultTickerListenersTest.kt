package com.autocoin.exchangegateway.api.exchange.ticker

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.ticker.listener.TickerListener
import com.autocoin.exchangegateway.spi.exchange.ticker.listener.TickerListeners
import com.autocoin.exchangegateway.spi.exchange.ticker.listener.TickerRegistrationListener
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair as SpiCurrencyPair
import com.autocoin.exchangegateway.spi.exchange.ticker.Ticker as SpiTicker

class TestTickerListener : TickerListener {
    override fun onTicker(
        exchangeName: ExchangeName,
        currencyPair: SpiCurrencyPair,
        ticker: SpiTicker,
    ) {
    }
}


@ExtendWith(MockitoExtension::class)
class DefaultTickerListenersTest {
    private lateinit var tested: TickerListeners
    private val currencyPair_AB = CurrencyPair.of("A/B")
    private val currencyPair_CD = CurrencyPair.of("C/D")
    private val tickerListener = TestTickerListener()
    private val bittrex = ExchangeName("bittrex")
    private val binance = ExchangeName("binance")

    @BeforeEach
    fun setup() {
        tested = DefaultTickerListeners()
    }

    @Test
    fun shouldAddTickerListenersForDifferentExchanges() {
        // when
        tested.addTickerListener(bittrex, currencyPair_AB, tickerListener)
        tested.addTickerListener(binance, currencyPair_CD, tickerListener)
        // then
        assertThat(tested.getTickerListeners(bittrex)).isEqualTo(
            mapOf(
                currencyPair_AB to setOf(tickerListener),
            ),
        )
        assertThat(tested.getTickerListeners(binance)).isEqualTo(
            mapOf(
                currencyPair_CD to setOf(tickerListener),
            ),
        )
    }

    @Test
    fun shouldAddTickerListenersForTheSameExchanges() {
        // when
        tested.addTickerListener(bittrex, currencyPair_AB, tickerListener)
        tested.addTickerListener(bittrex, currencyPair_CD, tickerListener)
        // then
        assertThat(tested.getTickerListeners(bittrex)).isEqualTo(
            mapOf(
                currencyPair_AB to setOf(tickerListener),
                currencyPair_CD to setOf(tickerListener),
            ),
        )
    }

    @Test
    fun shouldRemoveTickerListeners() {
        // when
        tested.addTickerListener(bittrex, currencyPair_AB, tickerListener)
        tested.addTickerListener(binance, currencyPair_CD, tickerListener)
        tested.removeTickerListener(bittrex, currencyPair_AB, tickerListener)
        // then
        assertThat(tested.getTickerListeners(bittrex)).isEqualTo(emptyMap<CurrencyPair, Set<TickerListener>>())
        assertThat(tested.getTickerListeners(binance)).isEqualTo(
            mapOf(
                currencyPair_CD to setOf(tickerListener),
            ),
        )
    }

    @Test
    fun shouldNotifyRegistrationListenerAboutFirstExchangeListenerAdded() {
        // given
        val registrationListener = mock<TickerRegistrationListener>()
        tested.addTickerRegistrationListener(registrationListener)
        // when
        tested.addTickerListener(binance, currencyPair_AB, tickerListener)
        // then
        verify(registrationListener).onFirstListenerRegistered(binance)
    }

    @Test
    fun shouldNotifyRegistrationListenerAboutLastExchangeListenerRemoved() {
        // given
        val registrationListener = mock<TickerRegistrationListener>()
        tested.addTickerRegistrationListener(registrationListener)
        // when
        tested.addTickerListener(binance, currencyPair_AB, tickerListener)
        tested.removeTickerListener(binance, currencyPair_AB, tickerListener)
        // then
        verify(registrationListener).onLastListenerDeregistered(binance)
    }

}
