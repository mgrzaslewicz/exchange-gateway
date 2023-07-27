package com.autocoin.exchangegateway.api.exchange.ticker

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.Exchange
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
        exchange: Exchange,
        currencyPair: SpiCurrencyPair,
        ticker: SpiTicker,
    ) {
    }
}


@ExtendWith(MockitoExtension::class)
class DefaultTickerListenersTest {
    private lateinit var tested: TickerListeners
    private val currencyPairAB = CurrencyPair.of("A/B")
    private val currencyPairCD = CurrencyPair.of("C/D")
    private val tickerListener = TestTickerListener()
    private val exchangeA = object : Exchange {
        override val exchangeName = "a"
    }
    private val exchangeB = object : Exchange {
        override val exchangeName = "b"
    }

    @BeforeEach
    fun setup() {
        tested = DefaultTickerListeners()
    }

    @Test
    fun shouldAddTickerListenersForDifferentExchanges() {
        // when
        tested.addTickerListener(exchangeA, currencyPairAB, tickerListener)
        tested.addTickerListener(exchangeB, currencyPairCD, tickerListener)
        // then
        assertThat(tested.getTickerListeners(exchangeA)).isEqualTo(
            mapOf(
                currencyPairAB to setOf(tickerListener),
            ),
        )
        assertThat(tested.getTickerListeners(exchangeB)).isEqualTo(
            mapOf(
                currencyPairCD to setOf(tickerListener),
            ),
        )
    }

    @Test
    fun shouldAddTickerListenersForTheSameExchanges() {
        // when
        tested.addTickerListener(exchangeA, currencyPairAB, tickerListener)
        tested.addTickerListener(exchangeA, currencyPairCD, tickerListener)
        // then
        assertThat(tested.getTickerListeners(exchangeA)).isEqualTo(
            mapOf(
                currencyPairAB to setOf(tickerListener),
                currencyPairCD to setOf(tickerListener),
            ),
        )
    }

    @Test
    fun shouldRemoveTickerListeners() {
        // when
        tested.addTickerListener(exchangeA, currencyPairAB, tickerListener)
        tested.addTickerListener(exchangeB, currencyPairCD, tickerListener)
        tested.removeTickerListener(exchangeA, currencyPairAB, tickerListener)
        // then
        assertThat(tested.getTickerListeners(exchangeA)).isEqualTo(emptyMap<CurrencyPair, Set<TickerListener>>())
        assertThat(tested.getTickerListeners(exchangeB)).isEqualTo(
            mapOf(
                currencyPairCD to setOf(tickerListener),
            ),
        )
    }

    @Test
    fun shouldNotifyRegistrationListenerAboutFirstExchangeListenerAdded() {
        // given
        val registrationListener = mock<TickerRegistrationListener>()
        tested.addTickerRegistrationListener(registrationListener)
        // when
        tested.addTickerListener(exchangeB, currencyPairAB, tickerListener)
        // then
        verify(registrationListener).onFirstListenerRegistered(exchangeB)
    }

    @Test
    fun shouldNotifyRegistrationListenerAboutLastExchangeListenerRemoved() {
        // given
        val registrationListener = mock<TickerRegistrationListener>()
        tested.addTickerRegistrationListener(registrationListener)
        // when
        tested.addTickerListener(exchangeB, currencyPairAB, tickerListener)
        tested.removeTickerListener(exchangeB, currencyPairAB, tickerListener)
        // then
        verify(registrationListener).onLastListenerDeregistered(exchangeB)
    }

}
