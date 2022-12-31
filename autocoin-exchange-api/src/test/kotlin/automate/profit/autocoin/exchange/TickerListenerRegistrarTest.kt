package automate.profit.autocoin.exchange

import automate.profit.autocoin.exchange.SupportedExchange.BITTREX
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.ticker.*
import com.nhaarman.mockito_kotlin.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant


class TickerListenerRegistrarTest {

    private val userExchangeTickerService = mock<UserExchangeTickerService>()

    private val tickerListenerRegistrar: TickerListenerRegistrar = DefaultTickerListenerRegistrar(BITTREX, userExchangeTickerService)

    private val tickerListener: TickerListener = mock()
    private val tickerListener2: TickerListener = mock()
    private val tickerListener3: TickerListener = mock()
    private val tickerListener4: TickerListener = mock()

    private val bchBtcCurrencyPair = CurrencyPair.of("BCH/BTC")
    private val ethBtcCurrencyPair = CurrencyPair.of("ETH/BTC")


    private var bchBtcTicker: Ticker = createTicker(bchBtcCurrencyPair, Instant.EPOCH)
    private var bchBtcTickerNoTimestamp: Ticker = createTicker(bchBtcCurrencyPair, null)
    private var bchBtcTicker2NoTimestamp: Ticker = createTicker(bchBtcCurrencyPair, null)
    private var ethBtcTickerNoTimestamp: Ticker = createTicker(ethBtcCurrencyPair, null)

    @Test
    fun shouldNotifyEachRegisteredTickerListenerOnNewTicker() {
        // given
        newTickersEveryTime(bchBtcCurrencyPair)
        newTickersEveryTime(ethBtcCurrencyPair)
        registerTickerListener(bchBtcCurrencyPair, tickerListener)
        registerTickerListener(ethBtcCurrencyPair, tickerListener2)
        // when
        fetchTickers()
        // then
        verify(tickerListener).onTicker(check { ticker -> assertThat(ticker.currencyPair).isEqualTo(bchBtcCurrencyPair) })
        verify(tickerListener2).onTicker(check { ticker -> assertThat(ticker.currencyPair).isEqualTo(ethBtcCurrencyPair) })
    }

    @Test
    fun shouldNotifyRegisteredTickerListenerWhenOneListenerPerCurrencyPair() {
        // given
        newTickersEveryTime(bchBtcCurrencyPair)
        newTickersEveryTime(ethBtcCurrencyPair)
        registerTickerListener(bchBtcCurrencyPair, tickerListener)
        registerTickerListener(bchBtcCurrencyPair, tickerListener2)
        registerTickerListener(ethBtcCurrencyPair, tickerListener3)
        registerTickerListener(ethBtcCurrencyPair, tickerListener4)
        // when
        fetchTickers()
        // then
        verify(tickerListener).onTicker(check { ticker -> assertThat(ticker.currencyPair).isEqualTo(bchBtcCurrencyPair) })
        verify(tickerListener2).onTicker(check { ticker -> assertThat(ticker.currencyPair).isEqualTo(bchBtcCurrencyPair) })
        verify(tickerListener3).onTicker(check { ticker -> assertThat(ticker.currencyPair).isEqualTo(ethBtcCurrencyPair) })
        verify(tickerListener4).onTicker(check { ticker -> assertThat(ticker.currencyPair).isEqualTo(ethBtcCurrencyPair) })
    }

    @Test
    fun shouldNotNotifyRemovedTickerListener() {
        // given
        newTickersEveryTime(bchBtcCurrencyPair)
        registerTickerListener(bchBtcCurrencyPair, tickerListener)
        tickerListenerRegistrar.removeTickerListener(tickerListener)
        // when
        fetchTickers()
        // then
        verify(tickerListener, never()).onTicker(any())
    }

    @Test
    fun shouldNotifyRegisteredTickerListenersWhenMultipleListenersPerCurrencyPair() {
        // given
        newTickersEveryTime(bchBtcCurrencyPair)
        newTickersEveryTime(ethBtcCurrencyPair)
        registerTickerListener(bchBtcCurrencyPair, tickerListener)
        registerTickerListener(ethBtcCurrencyPair, tickerListener2)
        // when
        fetchTickers()
        // then
        verify(tickerListener).onTicker(check { ticker -> assertThat(ticker.currencyPair).isEqualTo(bchBtcCurrencyPair) })
        verify(tickerListener2).onTicker(check { ticker -> assertThat(ticker.currencyPair).isEqualTo(ethBtcCurrencyPair) })
    }

    @Test
    fun shouldNotAllowToRegisterTheSameListenerTwice() {
        // given
        newTickersEveryTime(bchBtcCurrencyPair)
        registerTickerListener(bchBtcCurrencyPair, tickerListener)
        // when
        val addedSecondTime = registerTickerListener(bchBtcCurrencyPair, tickerListener)
        // then
        assertThat(addedSecondTime).isEqualTo(false)
    }

    @Test
    fun shouldNotNotifyTheSameTickerListenerTwice() {
        // given
        sameBchTickerEveryTime()
        registerTickerListener()
        // when
        fetchTickersTwice()
        // then
        verify(tickerListener, times(1)).onTicker(any())
    }

    @Test
    fun shouldNotPublishTheSameTickTwiceWhenSecondLessThanSecondLater() {
        // given
        sameBchTickerEveryTwiceButSecond10msecLater()
        registerTickerListener()
        // when
        fetchTickersTwice()
        // then
        verify(tickerListener, times(1)).onTicker(any())
    }

    @Test
    fun shouldPublishTwoDifferentTickersWithoutTimestamp() {
        // given
        twoNewBchTickersNoTimestamp()
        registerTickerListener()
        // when
        fetchTickersTwice()
        // then
        verify(tickerListener).onTicker(bchBtcTickerNoTimestamp)
        verify(tickerListener).onTicker(bchBtcTicker2NoTimestamp)
    }

    @Test
    fun shouldPublishTwoDifferentTickersFromDifferentCurrencyPairsWithoutTimestamp() {
        // given
        bchThenEthTickerNoTimestamp()
        registerTickerListener(bchBtcCurrencyPair)
        registerTickerListener(ethBtcCurrencyPair)
        // when
        fetchTickers()
        // then
        verify(tickerListener).onTicker(bchBtcTickerNoTimestamp)
        verify(tickerListener).onTicker(ethBtcTickerNoTimestamp)
    }

    @Test
    fun shouldNotPublishTheSameTickTwiceNoTimestamp() {
        // given
        sameBchTickerEveryTimeNoTimestamp()
        registerTickerListener()
        // when
        fetchTickersTwice()
        // then
        verify(tickerListener, times(1)).onTicker(any())
    }

    @Test
    fun shouldGetListenersOfClass() {
        // given
        class A : TickerListener {
            override fun onTicker(ticker: Ticker) {}
            override fun currencyPair(): CurrencyPair = bchBtcCurrencyPair
        }

        class B : TickerListener {
            override fun onTicker(ticker: Ticker) {}
            override fun currencyPair(): CurrencyPair = bchBtcCurrencyPair
        }

        val a = A()
        tickerListenerRegistrar.registerTickerListener(a)
        tickerListenerRegistrar.registerTickerListener(B())
        // when
        val listenersOfClass = tickerListenerRegistrar.getListenersOfClass(A::class.java)
        // then
        assertThat(listenersOfClass[bchBtcCurrencyPair]).containsExactly(a)
    }

    private fun registerTickerListener(currencyPair: CurrencyPair = bchBtcCurrencyPair, listener: TickerListener = tickerListener): Boolean {
        whenever(listener.currencyPair()).thenReturn(currencyPair)
        return tickerListenerRegistrar.registerTickerListener(listener)
    }

    private fun fetchTickers() {
        runBlocking {
            tickerListenerRegistrar.fetchTickersAndNotifyListeners()
        }
    }

    private fun fetchTickersTwice() {
        fetchTickers()
        fetchTickers()
    }

    private fun newTickersEveryTime(currencyPair: CurrencyPair = bchBtcCurrencyPair) {
        whenever(userExchangeTickerService.getTicker(currencyPair)).thenAnswer { createTicker(currencyPair) }
    }

    private fun sameBchTickerEveryTime() {
        whenever(userExchangeTickerService.getTicker(bchBtcCurrencyPair)).thenReturn(bchBtcTicker)
    }

    private fun sameBchTickerEveryTwiceButSecond10msecLater() {
        whenever(userExchangeTickerService.getTicker(bchBtcCurrencyPair)).thenReturn(bchBtcTicker, createTicker(bchBtcCurrencyPair, Instant.EPOCH.plusMillis(10)))
    }

    private fun twoNewBchTickersNoTimestamp() {
        whenever(userExchangeTickerService.getTicker(bchBtcCurrencyPair)).thenReturn(bchBtcTickerNoTimestamp, bchBtcTicker2NoTimestamp)
    }

    private fun bchThenEthTickerNoTimestamp() {
        whenever(userExchangeTickerService.getTicker(bchBtcCurrencyPair)).thenReturn(bchBtcTickerNoTimestamp)
        whenever(userExchangeTickerService.getTicker(ethBtcCurrencyPair)).thenReturn(ethBtcTickerNoTimestamp)
    }

    private fun sameBchTickerEveryTimeNoTimestamp() {
        whenever(userExchangeTickerService.getTicker(bchBtcCurrencyPair)).thenReturn(bchBtcTickerNoTimestamp)
    }

    private companion object ValueGenerator {
        val generator = generateSequence(1) { it + 1 }.iterator()
        fun next() = generator.next()
    }

    private fun createTicker(currencyPair: CurrencyPair, timestamp: Instant? = Instant.now()): Ticker {
        val baseValue = next()
        return Ticker(
                currencyPair = currencyPair,
                last = BigDecimal(baseValue + 1.1),
                bid = BigDecimal(baseValue + 1.2),
                ask = BigDecimal(baseValue + 1.1),
                timestamp = timestamp
        )
    }

}
