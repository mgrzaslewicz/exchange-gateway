package automate.profit.autocoin.exchange.ticker

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.SupportedExchange.BITBAY
import automate.profit.autocoin.exchange.SupportedExchange.BITTREX
import automate.profit.autocoin.exchange.currency.CurrencyPair
import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

class TestTickerListener(private val currencyPair: CurrencyPair, private val supportedExchange: SupportedExchange) : TickerListener {
    override fun onTicker(ticker: Ticker) {}
    override fun currencyPair() = currencyPair
    override fun exchange() = supportedExchange
}


@ExtendWith(MockitoExtension::class)
class TickerListenerRegistrarsTest {

    private lateinit var bittrexTickerListenerRegistrar: TickerListenerRegistrar
    private lateinit var bitbayTickerListenerRegistrar: TickerListenerRegistrar

    private lateinit var tickerListenerRegistrarList: List<TickerListenerRegistrar>
    private lateinit var tickerListenerRegistrars: TickerListenerRegistrars
    private val btcLtc = CurrencyPair.of("BTC/LTC")

    @BeforeEach
    fun setup() {
        bittrexTickerListenerRegistrar = spy(DefaultTickerListenerRegistrar(BITTREX, mock()))
        bitbayTickerListenerRegistrar = spy(DefaultTickerListenerRegistrar(BITBAY, mock()))
        tickerListenerRegistrarList = listOf(bittrexTickerListenerRegistrar, bitbayTickerListenerRegistrar)
        tickerListenerRegistrars = DefaultTickerListenerRegistrars(listOf(bittrexTickerListenerRegistrar, bitbayTickerListenerRegistrar), mock())
    }

    @Test
    fun shouldRegisterTickerListener() {
        // given
        val listener = TestTickerListener(btcLtc, BITTREX)
        // when
        tickerListenerRegistrars.registerTickerListener(listener)
        // then
        verify(bittrexTickerListenerRegistrar).registerTickerListener(listener)
        verify(bitbayTickerListenerRegistrar, times(0)).registerTickerListener(listener)
    }

    @Test
    fun shouldRegisterTickerListenerWhenCreatingTickerListenerRegistrarOnTheFly() {
        // given
        val listener = TestTickerListener(btcLtc, BITTREX)
        val tickerListenerRegistrarProvider = mock<TickerListenerRegistrarProvider>().apply {
            whenever(this.createTickerListenerRegistrar(BITTREX)).thenReturn(bittrexTickerListenerRegistrar)
        }
        tickerListenerRegistrars = DefaultTickerListenerRegistrars(emptyList(), tickerListenerRegistrarProvider)
        // when
        tickerListenerRegistrars.registerTickerListener(listener)
        // then
        verify(bittrexTickerListenerRegistrar).registerTickerListener(listener)
    }

    @Test
    fun shouldGetListenersOfClass() {
        // given
        tickerListenerRegistrars = DefaultTickerListenerRegistrars(tickerListenerRegistrarList, mock())
        val listener = TestTickerListener(btcLtc, BITTREX)
        tickerListenerRegistrars.registerTickerListener(listener)
        // when
        val listeners = tickerListenerRegistrars.getListenersOfClassList(TestTickerListener::class.java)
        // then
        assertThat(listeners).containsOnly(listener)
    }

    @Test
    fun shouldRemoveRegisteredTickerListener() {
        // given
        val listener = TestTickerListener(btcLtc, BITTREX)
        tickerListenerRegistrars.registerTickerListener(listener)
        // when
        tickerListenerRegistrars.removeTickerListener(listener)
        // then
        verify(bittrexTickerListenerRegistrar).removeTickerListener(listener)
    }

    @Test
    fun shouldFailWhenAddingExchangeDuplicates() {
        assertThrows<IllegalArgumentException> {
            DefaultTickerListenerRegistrars(listOf(bittrexTickerListenerRegistrar, bitbayTickerListenerRegistrar, bitbayTickerListenerRegistrar), mock())
        }

    }

}
