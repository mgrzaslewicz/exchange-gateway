package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.SupportedExchange.BITBAY
import automate.profit.autocoin.exchange.SupportedExchange.BITTREX
import automate.profit.autocoin.exchange.currency.CurrencyPair
import com.google.common.util.concurrent.MoreExecutors
import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

class TestOrderBookListener(private val currencyPair: CurrencyPair, private val supportedExchange: SupportedExchange) : OrderBookListener {
    override fun onOrderBook(orderBook: OrderBook) {}
    override fun currencyPair() = currencyPair
    override fun exchange() = supportedExchange
}


@ExtendWith(MockitoExtension::class)
class OrderBookListenerRegistrarsTest {

    private lateinit var bittrexListenerRegistrar: OrderBookListenerRegistrar
    private lateinit var bitbayListenerRegistrar: OrderBookListenerRegistrar

    private lateinit var tickerListenerRegistrarList: List<OrderBookListenerRegistrar>
    private lateinit var tickerListenerRegistrars: OrderBookListenerRegistrars
    private val btcLtc = CurrencyPair.of("BTC/LTC")
    private val executorRunningSynchronouslyOnTheSameThread = MoreExecutors.newDirectExecutorService()

    @BeforeEach
    fun setup() {
        bittrexListenerRegistrar = spy(DefaultOrderBookListenerRegistrar(
                exchangeName = BITTREX,
                userExchangeOrderBookService = mock(),
                executorService = executorRunningSynchronouslyOnTheSameThread
        ))
        bitbayListenerRegistrar = spy(DefaultOrderBookListenerRegistrar(
                exchangeName = BITBAY,
                userExchangeOrderBookService = mock(),
                executorService = executorRunningSynchronouslyOnTheSameThread
        ))
        tickerListenerRegistrarList = listOf(bittrexListenerRegistrar, bitbayListenerRegistrar)
        tickerListenerRegistrars = DefaultOrderBookListenerRegistrars(
                initialTickerListenerRegistrarList = listOf(bittrexListenerRegistrar, bitbayListenerRegistrar),
                orderBookListenerRegistrarProvider = mock(),
                executorService = executorRunningSynchronouslyOnTheSameThread
        )
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
        tickerListenerRegistrars = DefaultOrderBookListenerRegistrars(
                initialTickerListenerRegistrarList = emptyList(),
                orderBookListenerRegistrarProvider = tickerListenerRegistrarProvider,
                executorService = executorRunningSynchronouslyOnTheSameThread
        )
        // when
        tickerListenerRegistrars.registerOrderBookListener(listener)
        // then
        verify(bittrexListenerRegistrar).registerOrderBookListener(listener)
    }

    @Test
    fun shouldGetListenersOfClass() {
        // given
        tickerListenerRegistrars = DefaultOrderBookListenerRegistrars(
                initialTickerListenerRegistrarList = tickerListenerRegistrarList,
                orderBookListenerRegistrarProvider = mock(),
                executorService = executorRunningSynchronouslyOnTheSameThread
        )
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

    @Test
    fun shouldFailWhenAddingExchangeDuplicates() {
        assertThrows<IllegalArgumentException> {
            DefaultOrderBookListenerRegistrars(
                    initialTickerListenerRegistrarList = listOf(bittrexListenerRegistrar, bitbayListenerRegistrar, bitbayListenerRegistrar),
                    orderBookListenerRegistrarProvider = mock(),
                    executorService = executorRunningSynchronouslyOnTheSameThread
            )
        }

    }

}
