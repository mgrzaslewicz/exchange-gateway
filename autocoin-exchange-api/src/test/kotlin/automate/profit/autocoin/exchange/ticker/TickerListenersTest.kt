package automate.profit.autocoin.exchange.ticker

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.SupportedExchange.BINANCE
import automate.profit.autocoin.exchange.SupportedExchange.BITTREX
import automate.profit.autocoin.exchange.currency.CurrencyPair
import com.google.common.util.concurrent.MoreExecutors
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

class TestTickerListener : TickerListener {
    override fun onTicker(exchange: SupportedExchange, currencyPair: CurrencyPair, ticker: Ticker) {}
}


@ExtendWith(MockitoExtension::class)
class TickerListenersTest {
    private lateinit var tested: TickerListeners
    private val currencyPair_AB = CurrencyPair.of("A/B")
    private val currencyPair_CD = CurrencyPair.of("C/D")
    private val tickerListener = TestTickerListener()

    @BeforeEach
    fun setup() {
        tested = DefaultTickerListeners(MoreExecutors.newDirectExecutorService())
    }

    @Test
    fun shouldAddTickerListenersForDifferentExchanges() {
        // when
        tested.addTickerListener(BITTREX, currencyPair_AB, tickerListener)
        tested.addTickerListener(BINANCE, currencyPair_CD, tickerListener)
        // then
        assertThat(tested.getTickerListeners(BITTREX)).isEqualTo(mapOf(
                currencyPair_AB to setOf(tickerListener)
        ))
        assertThat(tested.getTickerListeners(BINANCE)).isEqualTo(mapOf(
                currencyPair_CD to setOf(tickerListener)
        ))
    }

    @Test
    fun shouldAddTickerListenersForTheSameExchanges() {
        // when
        tested.addTickerListener(BITTREX, currencyPair_AB, tickerListener)
        tested.addTickerListener(BITTREX, currencyPair_CD, tickerListener)
        // then
        assertThat(tested.getTickerListeners(BITTREX)).isEqualTo(mapOf(
                currencyPair_AB to setOf(tickerListener),
                currencyPair_CD to setOf(tickerListener)
        ))
    }

    @Test
    fun shouldRemoveTickerListeners() {
        // when
        tested.addTickerListener(BITTREX, currencyPair_AB, tickerListener)
        tested.addTickerListener(BINANCE, currencyPair_CD, tickerListener)
        tested.removeTickerListener(BITTREX, currencyPair_AB, tickerListener)
        // then
        assertThat(tested.getTickerListeners(BITTREX)).isEqualTo(emptyMap<CurrencyPair, Set<TickerListener>>())
        assertThat(tested.getTickerListeners(BINANCE)).isEqualTo(mapOf(
                currencyPair_CD to setOf(tickerListener)
        ))
    }

    @Test
    fun shouldNotifyRegistrationListenerAboutFirstExchangeListenerAdded() {
        // given
        val registrationListener = mock<TickerRegistrationListener>()
        tested.addTickerRegistrationListener(registrationListener)
        // when
        tested.addTickerListener(BINANCE, currencyPair_AB, tickerListener)
        // then
        verify(registrationListener).onFirstListenerRegistered(BINANCE)
    }

    @Test
    fun shouldNotifyRegistrationListenerAboutLastExchangeListenerRemoved() {
        // given
        val registrationListener = mock<TickerRegistrationListener>()
        tested.addTickerRegistrationListener(registrationListener)
        // when
        tested.addTickerListener(BINANCE, currencyPair_AB, tickerListener)
        tested.removeTickerListener(BINANCE, currencyPair_AB, tickerListener)
        // then
        verify(registrationListener).onLastListenerDeregistered(BINANCE)
    }

}
