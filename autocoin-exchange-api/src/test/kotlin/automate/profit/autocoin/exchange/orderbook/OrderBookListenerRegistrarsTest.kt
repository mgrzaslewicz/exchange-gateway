package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.SupportedExchange.BITBAY
import automate.profit.autocoin.exchange.SupportedExchange.BITTREX
import automate.profit.autocoin.exchange.currency.CurrencyPair
import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

class TestOrderBookListener(private val currencyPair: CurrencyPair, private val supportedExchange: SupportedExchange) : OrderBookListener {
    override fun onOrderBook(orderBook: OrderBook) {}
    override fun currencyPair() = currencyPair
    override fun exchange() = supportedExchange
}


@RunWith(MockitoJUnitRunner::class)
class OrderBookListenerRegistrarsTest {

    private lateinit var bittrexListenerRegistrar: OrderBookListenerRegistrar
    private lateinit var bitbayListenerRegistrar: OrderBookListenerRegistrar

    private lateinit var tickerListenerRegistrarList: List<OrderBookListenerRegistrar>
    private lateinit var tickerListenerRegistrars: OrderBookListenerRegistrars
    private val btcLtc = CurrencyPair.of("BTC/LTC")

    @Before
    fun setup() {
        bittrexListenerRegistrar = spy(DefaultOrderBookListenerRegistrar(BITTREX, mock()))
        bitbayListenerRegistrar = spy(DefaultOrderBookListenerRegistrar(BITBAY, mock()))
        tickerListenerRegistrarList = listOf(bittrexListenerRegistrar, bitbayListenerRegistrar)
        tickerListenerRegistrars = DefaultOrderBookListenerRegistrars(listOf(bittrexListenerRegistrar, bitbayListenerRegistrar), mock())
    }

    @Test
    fun shouldRegisterOrderBookListener() {
        // given
        val listener = TestOrderBookListener(btcLtc, BITTREX)
        // when
        tickerListenerRegistrars.registerOrderBookListener(listener)
        // then
        verify(bittrexListenerRegistrar).registerOrderBookListener(listener)
        verify(bitbayListenerRegistrar, times(0)).registerOrderBookListener(listener)
    }

    @Test
    fun shouldRegisterOrderBookListenerWhenCreatingOrderBookListenerRegistrarOnTheFly() {
        // given
        val listener = TestOrderBookListener(btcLtc, BITTREX)
        val tickerListenerRegistrarProvider = mock<OrderBookListenerRegistrarProvider>().apply {
            whenever(this.createOrderBookListenerRegistrar(BITTREX)).thenReturn(bittrexListenerRegistrar)
        }
        tickerListenerRegistrars = DefaultOrderBookListenerRegistrars(emptyList(), tickerListenerRegistrarProvider)
        // when
        tickerListenerRegistrars.registerOrderBookListener(listener)
        // then
        verify(bittrexListenerRegistrar).registerOrderBookListener(listener)
    }

    @Test
    fun shouldGetListenersOfClass() {
        // given
        tickerListenerRegistrars = DefaultOrderBookListenerRegistrars(tickerListenerRegistrarList, mock())
        val listener = TestOrderBookListener(btcLtc, BITTREX)
        tickerListenerRegistrars.registerOrderBookListener(listener)
        // when
        val listeners = tickerListenerRegistrars.getListenersOfClassList(TestOrderBookListener::class.java)
        // then
        assertThat(listeners).containsOnly(listener)
    }

    @Test
    fun shouldRemoveRegisteredOrderBookListener() {
        // given
        val listener = TestOrderBookListener(btcLtc, BITTREX)
        tickerListenerRegistrars.registerOrderBookListener(listener)
        // when
        tickerListenerRegistrars.removeOrderBookListener(listener)
        // then
        verify(bittrexListenerRegistrar).removeOrderBookListener(listener)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWhenAddingExchangeDuplicates() {
        DefaultOrderBookListenerRegistrars(listOf(bittrexListenerRegistrar, bitbayListenerRegistrar, bitbayListenerRegistrar), mock())
    }

}
