package automate.profit.autocoin.exchange

import automate.profit.autocoin.exchange.SupportedExchange.*
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.ticker.*
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

class TestTickerListener(private val currencyPair: CurrencyPair) : TickerListener {
    override fun onTicker(ticker: Ticker) {}
    override fun currencyPair() = currencyPair
}


@RunWith(MockitoJUnitRunner::class)
class TickerListenerRegistrarsTest {

    private lateinit var bittrexTickerListenerRegistrar: TickerListenerRegistrar
    private lateinit var bitbayTickerListenerRegistrar: TickerListenerRegistrar

    private lateinit var tickerListenerRegistrarList: List<TickerListenerRegistrar>
    private lateinit var tickerListenerRegistrars: TickerListenerRegistrars
    private val btcLtc = CurrencyPair.of("BTC/LTC")

    @Before
    fun setup() {
        bittrexTickerListenerRegistrar = spy(DefaultTickerListenerRegistrar(BITTREX, mock()))
        bitbayTickerListenerRegistrar = spy(DefaultTickerListenerRegistrar(BITBAY, mock()))
        tickerListenerRegistrarList = listOf(bittrexTickerListenerRegistrar, bitbayTickerListenerRegistrar)
        tickerListenerRegistrars = DefaultTickerListenerRegistrars(listOf(bittrexTickerListenerRegistrar, bitbayTickerListenerRegistrar), mock())
    }

    @Test
    fun shouldRegisterTickerListener() {
        // given
        val listener = TestTickerListener(btcLtc)
        // when
        tickerListenerRegistrars.registerTickerListener(BITTREX, listener)
        // then
        verify(bittrexTickerListenerRegistrar).registerTickerListener(listener)
        verify(bitbayTickerListenerRegistrar, times(0)).registerTickerListener(listener)
    }

    @Test
    fun shouldRegisterTickerListenerWhenCreatingTickerListenerRegistrarOnTheFly() {
        // given
        val listener = TestTickerListener(btcLtc)
        val tickerListenerRegistrarProvider = mock<TickerListenerRegistrarProvider>().apply {
            whenever(this.createTickerListenerRegistrar(BITTREX)).thenReturn(bittrexTickerListenerRegistrar)
        }
        tickerListenerRegistrars = DefaultTickerListenerRegistrars(emptyList(), tickerListenerRegistrarProvider)
        // when
        tickerListenerRegistrars.registerTickerListener(BITTREX, listener)
        // then
        verify(bittrexTickerListenerRegistrar).registerTickerListener(listener)
    }

    @Test
    fun shouldGetListenersOfClass() {
        // given
        tickerListenerRegistrars = DefaultTickerListenerRegistrars(tickerListenerRegistrarList, mock())
        val listener = TestTickerListener(btcLtc)
        tickerListenerRegistrars.registerTickerListener(BITTREX, listener)
        // when
        val listeners = tickerListenerRegistrars.getListenersOfClassList(TestTickerListener::class.java)
        // then
        assertThat(listeners).containsOnly(listener)
    }

    @Test
    fun shouldRemoveRegisteredTickerListener() {
        // given
        val listener = TestTickerListener(btcLtc)
        tickerListenerRegistrars.registerTickerListener(BITTREX, listener)
        // when
        tickerListenerRegistrars.removeTickerListener(BITTREX, listener)
        // then
        verify(bittrexTickerListenerRegistrar).removeTickerListener(listener)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWhenAddingExchangeDuplicates() {
        DefaultTickerListenerRegistrars(listOf(bittrexTickerListenerRegistrar, bitbayTickerListenerRegistrar, bitbayTickerListenerRegistrar), mock())
    }

}
