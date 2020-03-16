package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.SupportedExchange.BITTREX
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.order.UserExchangeOrderBookService
import com.google.common.util.concurrent.MoreExecutors
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class OrderBookListenerRegistrarTest {

    private val userExchangeOrderBookService = mock<UserExchangeOrderBookService>()
    private val executorRunningSynchronouslyOnTheSameThread = MoreExecutors.newDirectExecutorService()
    private val tickerListenerRegistrar: OrderBookListenerRegistrar = DefaultOrderBookListenerRegistrar(BITTREX, userExchangeOrderBookService, executorRunningSynchronouslyOnTheSameThread)

    private val listener: OrderBookListener = mock()
    private val listener2: OrderBookListener = mock()
    private val listener3: OrderBookListener = mock()
    private val listener4: OrderBookListener = mock()

    private val bchBtcCurrencyPair = CurrencyPair.of("BCH/BTC")
    private val ethBtcCurrencyPair = CurrencyPair.of("ETH/BTC")


    private var bchBtcOrderBook: OrderBook = createOrderBook(1)
    private var bchBtcOrderBookNoTimestamp: OrderBook = createOrderBook(2)
    private var bchBtcOrderBook2NoTimestamp: OrderBook = createOrderBook(3)
    private var ethBtcOrderBookNoTimestamp: OrderBook = createOrderBook(4)

    @Test
    fun shouldNotifyEachRegisteredOrderBookListenerOnNewOrderBook() {
        // given
        val bchBtcOrderBooks = twoNewOrderBooks(bchBtcCurrencyPair)
        val ethBtcOrderBooks = twoNewOrderBooks(ethBtcCurrencyPair)
        registerOrderBookListener(bchBtcCurrencyPair, listener)
        registerOrderBookListener(ethBtcCurrencyPair, listener2)
        // when
        fetchOrderBooksTwice()
        // then
        verify(listener).onOrderBook(bchBtcOrderBooks[0])
        verify(listener).onOrderBook(bchBtcOrderBooks[1])
        verify(listener2).onOrderBook(ethBtcOrderBooks[0])
        verify(listener2).onOrderBook(ethBtcOrderBooks[1])
    }

    @Test
    fun shouldNotifyRegisteredOrderBookListenerWhenOneListenerPerCurrencyPair() {
        // given
        val bchBtcOrderBooks = twoNewOrderBooks(bchBtcCurrencyPair)
        val ethBtcOrderBooks = twoNewOrderBooks(ethBtcCurrencyPair)
        registerOrderBookListener(bchBtcCurrencyPair, listener)
        registerOrderBookListener(bchBtcCurrencyPair, listener2)
        registerOrderBookListener(ethBtcCurrencyPair, listener3)
        registerOrderBookListener(ethBtcCurrencyPair, listener4)
        // when
        fetchOrderBooksTwice()
        // then
        verify(listener).onOrderBook(bchBtcOrderBooks[0])
        verify(listener2).onOrderBook(bchBtcOrderBooks[1])
        verify(listener3).onOrderBook(ethBtcOrderBooks[0])
        verify(listener4).onOrderBook(ethBtcOrderBooks[1])
    }

    @Test
    fun shouldNotNotifyRemovedOrderBookListener() {
        // given
        twoNewOrderBooks(bchBtcCurrencyPair)
        registerOrderBookListener(bchBtcCurrencyPair, listener)
        tickerListenerRegistrar.removeOrderBookListener(listener)
        // when
        fetchOrderBooks()
        // then
        verify(listener, never()).onOrderBook(any())
    }

    @Test
    fun shouldNotifyRegisteredOrderBookListenersWhenMultipleListenersPerCurrencyPair() {
        // given
        val bchBtcOrderBooks = twoNewOrderBooks(bchBtcCurrencyPair)
        val ethBtcOrderBooks = twoNewOrderBooks(ethBtcCurrencyPair)
        registerOrderBookListener(bchBtcCurrencyPair, listener)
        registerOrderBookListener(bchBtcCurrencyPair, listener2)
        registerOrderBookListener(ethBtcCurrencyPair, listener3)
        registerOrderBookListener(ethBtcCurrencyPair, listener4)
        // when
        fetchOrderBooksTwice()
        // then
        verify(listener).onOrderBook(bchBtcOrderBooks[0])
        verify(listener2).onOrderBook(bchBtcOrderBooks[1])
        verify(listener3).onOrderBook(ethBtcOrderBooks[0])
        verify(listener4).onOrderBook(ethBtcOrderBooks[1])
    }

    @Test
    fun shouldNotAllowToRegisterTheSameListenerTwice() {
        // given
        twoNewOrderBooks(bchBtcCurrencyPair)
        registerOrderBookListener(bchBtcCurrencyPair, listener)
        // when
        val addedSecondTime = registerOrderBookListener(bchBtcCurrencyPair, listener)
        // then
        assertThat(addedSecondTime).isFalse()
    }

    @Test
    fun shouldNotNotifyTheSameOrderBookListenerTwice() {
        // given
        sameBchBtcOrderBookBookEveryTime()
        registerOrderBookListener()
        // when
        fetchOrderBooksTwice()
        // then
        verify(listener, times(1)).onOrderBook(any())
    }

    @Test
    fun shouldPublishTwoDifferentOrderBooksWithoutTimestamp() {
        // given
        twoNewOrderBooks()
        registerOrderBookListener()
        // when
        fetchOrderBooksTwice()
        // then
        verify(listener).onOrderBook(bchBtcOrderBookNoTimestamp)
        verify(listener).onOrderBook(bchBtcOrderBook2NoTimestamp)
    }

    @Test
    fun shouldPublishTwoDifferentOrderBooksFromDifferentCurrencyPairsWithoutTimestamp() {
        // given
        bchBtcThenEthBtcOrderBook()
        registerOrderBookListener(bchBtcCurrencyPair)
        registerOrderBookListener(ethBtcCurrencyPair)
        // when
        fetchOrderBooks()
        // then
        verify(listener).onOrderBook(bchBtcOrderBookNoTimestamp)
        verify(listener).onOrderBook(ethBtcOrderBookNoTimestamp)
    }

    @Test
    fun shouldNotPublishTheSameOrderBookTwice() {
        // given
        sameBchOrderBookEveryTime()
        registerOrderBookListener()
        // when
        fetchOrderBooksTwice()
        // then
        verify(listener, times(1)).onOrderBook(any())
    }

    @Test
    fun shouldNotifyNoNewOrderBook() {
        // given
        sameBchOrderBookEveryTime()
        registerOrderBookListener()
        // when
        fetchOrderBooksTwice()
        // then
        verify(listener, times(1)).onNoNewOrderBook(anyOrNull())
    }

    @Test
    fun shouldGetListenersOfClass() {
        // given
        class A : OrderBookListener {
            override fun onOrderBook(ticker: OrderBook) {}
            override fun currencyPair(): CurrencyPair = bchBtcCurrencyPair
            override fun exchange() = BITTREX
        }

        class B : OrderBookListener {
            override fun onOrderBook(ticker: OrderBook) {}
            override fun currencyPair(): CurrencyPair = bchBtcCurrencyPair
            override fun exchange() = BITTREX
        }

        val a = A()
        tickerListenerRegistrar.registerOrderBookListener(a)
        tickerListenerRegistrar.registerOrderBookListener(B())
        // when
        val listenersOfClass = tickerListenerRegistrar.getListenersOfClass(A::class.java)
        // then
        assertThat(listenersOfClass[bchBtcCurrencyPair]).containsExactly(a)
    }

    private fun registerOrderBookListener(currencyPair: CurrencyPair = bchBtcCurrencyPair, listener: OrderBookListener = this.listener): Boolean {
        whenever(listener.currencyPair()).thenReturn(currencyPair)
        return tickerListenerRegistrar.registerOrderBookListener(listener)
    }

    private fun fetchOrderBooks() {
        runBlocking {
            tickerListenerRegistrar.fetchOrderBooksAndNotifyListeners()
        }
    }

    private fun fetchOrderBooksTwice() {
        fetchOrderBooks()
        fetchOrderBooks()
    }

    private fun twoNewOrderBooks(currencyPair: CurrencyPair = bchBtcCurrencyPair): List<OrderBook> {
        val result = listOf(createOrderBook(1), createOrderBook(2))
        whenever(userExchangeOrderBookService.getOrderBook(currencyPair)).thenReturn(result[0], result[1])
        return result
    }

    private fun sameBchBtcOrderBookBookEveryTime() {
        whenever(userExchangeOrderBookService.getOrderBook(bchBtcCurrencyPair)).thenReturn(bchBtcOrderBook)
    }

    private fun twoNewOrderBooks() {
        whenever(userExchangeOrderBookService.getOrderBook(bchBtcCurrencyPair)).thenReturn(bchBtcOrderBookNoTimestamp, bchBtcOrderBook2NoTimestamp)
    }

    private fun bchBtcThenEthBtcOrderBook() {
        whenever(userExchangeOrderBookService.getOrderBook(bchBtcCurrencyPair)).thenReturn(bchBtcOrderBookNoTimestamp)
        whenever(userExchangeOrderBookService.getOrderBook(ethBtcCurrencyPair)).thenReturn(ethBtcOrderBookNoTimestamp)
    }

    private fun sameBchOrderBookEveryTime() {
        whenever(userExchangeOrderBookService.getOrderBook(bchBtcCurrencyPair)).thenReturn(bchBtcOrderBookNoTimestamp)
    }

    private companion object ValueGenerator {
        val generator = generateSequence(1) { it + 1 }.iterator()
        fun next() = generator.next()
    }

    private fun createOrderBook(howManyOrders: Int): OrderBook {
        return OrderBook(
                buyOrders = (1..howManyOrders).map { mock<OrderBookExchangeOrder>() },
                sellOrders = (1..howManyOrders).map { mock<OrderBookExchangeOrder>() }
        )
    }

}
