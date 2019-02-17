package automate.profit.autocoin.exchange

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.currency.toXchangeCurrencyPair
import automate.profit.autocoin.exchange.peruser.DefaultTickerListenerRegistrar
import automate.profit.autocoin.exchange.peruser.TickerListenerRegistrar
import automate.profit.autocoin.exchange.peruser.XchangeUserExchangeTickerProvider
import automate.profit.autocoin.ticker.Ticker
import automate.profit.autocoin.ticker.TickerListener
import automate.profit.autocoin.ticker.toXchangeTicker
import com.nhaarman.mockito_kotlin.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.knowm.xchange.service.marketdata.MarketDataService
import java.math.BigDecimal
import java.time.Instant


class TickerListenerRegistrarTest {

    private val xchangeMarketDataService: MarketDataService = mock()

    private val tickerListenerRegistrar: TickerListenerRegistrar = DefaultTickerListenerRegistrar(SupportedExchange.BITTREX, XchangeUserExchangeTickerProvider(xchangeMarketDataService))

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
        whenever(xchangeMarketDataService.getTicker(currencyPair.toXchangeCurrencyPair())).thenAnswer { createTicker(currencyPair).toXchangeTicker() }
    }

    private fun sameBchTickerEveryTime() {
        whenever(xchangeMarketDataService.getTicker(bchBtcCurrencyPair.toXchangeCurrencyPair())).thenReturn(bchBtcTicker.toXchangeTicker())
    }

    private fun sameBchTickerEveryTwiceButSecond10msecLater() {
        whenever(xchangeMarketDataService.getTicker(bchBtcCurrencyPair.toXchangeCurrencyPair())).thenReturn(bchBtcTicker.toXchangeTicker(), createTicker(bchBtcCurrencyPair, Instant.EPOCH.plusMillis(10)).toXchangeTicker())
    }

    private fun twoNewBchTickersNoTimestamp() {
        whenever(xchangeMarketDataService.getTicker(bchBtcCurrencyPair.toXchangeCurrencyPair())).thenReturn(bchBtcTickerNoTimestamp.toXchangeTicker(), bchBtcTicker2NoTimestamp.toXchangeTicker())
    }

    private fun bchThenEthTickerNoTimestamp() {
        whenever(xchangeMarketDataService.getTicker(bchBtcCurrencyPair.toXchangeCurrencyPair())).thenReturn(bchBtcTickerNoTimestamp.toXchangeTicker())
        whenever(xchangeMarketDataService.getTicker(ethBtcCurrencyPair.toXchangeCurrencyPair())).thenReturn(ethBtcTickerNoTimestamp.toXchangeTicker())
    }

    private fun sameBchTickerEveryTimeNoTimestamp() {
        whenever(xchangeMarketDataService.getTicker(bchBtcCurrencyPair.toXchangeCurrencyPair())).thenReturn(bchBtcTickerNoTimestamp.toXchangeTicker())
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
