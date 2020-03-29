package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.SupportedExchange.BINANCE
import automate.profit.autocoin.exchange.SupportedExchange.BITTREX
import automate.profit.autocoin.exchange.currency.CurrencyPair
import com.google.common.util.concurrent.MoreExecutors
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

class TestOrderBookListener : OrderBookListener {
    override fun onOrderBook(exchange: SupportedExchange, currencyPair: CurrencyPair, orderBook: OrderBook) {}
}


@ExtendWith(MockitoExtension::class)
class OrderBookListenersTest {
    private lateinit var tested: OrderBookListeners
    private val currencyPair_AB = CurrencyPair.of("A/B")
    private val currencyPair_CD = CurrencyPair.of("C/D")
    private val orderBookListener = TestOrderBookListener()

    @BeforeEach
    fun setup() {
        tested = DefaultOrderBookListeners(MoreExecutors.newDirectExecutorService())
    }

    @Test
    fun shouldAddOrderBookListenersForDifferentExchanges() {
        // given
        tested.addOrderBookListener(BITTREX, currencyPair_AB, orderBookListener)
        tested.addOrderBookListener(BINANCE, currencyPair_CD, orderBookListener)
        val orderBookListenerVisitor = mock<OrderBookListenersVisitor>()
        // when
        tested.iterateOverEachExchangeAndAllCurrencyPairs(orderBookListenerVisitor)
        // then
        verify(orderBookListenerVisitor).fetchOrderBooksThenNotifyListeners(BITTREX, mapOf(
                currencyPair_AB to setOf(orderBookListener)
        ))
        verify(orderBookListenerVisitor).fetchOrderBooksThenNotifyListeners(BINANCE, mapOf(
                currencyPair_CD to setOf(orderBookListener)
        ))
    }

    @Test
    fun shouldAddOrderBookListenersForTheSameExchanges() {
        // given
        tested.addOrderBookListener(BITTREX, currencyPair_AB, orderBookListener)
        tested.addOrderBookListener(BITTREX, currencyPair_CD, orderBookListener)
        val orderBookListenerVisitor = mock<OrderBookListenersVisitor>()
        // when
        tested.iterateOverEachExchangeAndAllCurrencyPairs(orderBookListenerVisitor)
        // then
        verify(orderBookListenerVisitor).fetchOrderBooksThenNotifyListeners(BITTREX, mapOf(
                currencyPair_AB to setOf(orderBookListener),
                currencyPair_CD to setOf(orderBookListener)
        ))
    }

    @Test
    fun shouldRemoveOrderBookListeners() {
        // given
        tested.addOrderBookListener(BITTREX, currencyPair_AB, orderBookListener)
        tested.addOrderBookListener(BINANCE, currencyPair_CD, orderBookListener)
        tested.removeOrderBookListener(BITTREX, currencyPair_AB, orderBookListener)
        val orderBookListenerVisitor = mock<OrderBookListenersVisitor>()
        // when
        tested.iterateOverEachExchangeAndAllCurrencyPairs(orderBookListenerVisitor)
        // then
        verify(orderBookListenerVisitor, never()).fetchOrderBooksThenNotifyListeners(eq(BITTREX), any())
        verify(orderBookListenerVisitor).fetchOrderBooksThenNotifyListeners(BINANCE, mapOf(
                currencyPair_CD to setOf(orderBookListener)
        ))
    }

    @Test
    fun shouldNotifyRegistrationListenerAboutFirstExchangeListenerAdded() {
        // given
        val registrationListener = mock<OrderBookRegistrationListener>()
        tested.addOrderBookRegistrationListener(registrationListener)
        // when
        tested.addOrderBookListener(BINANCE, currencyPair_AB, orderBookListener)
        // then
        verify(registrationListener).onFirstListenerRegistered(BINANCE)
    }

    @Test
    fun shouldNotifyRegistrationListenerAboutLastExchangeListenerRemoved() {
        // given
        val registrationListener = mock<OrderBookRegistrationListener>()
        tested.addOrderBookRegistrationListener(registrationListener)
        // when
        tested.addOrderBookListener(BINANCE, currencyPair_AB, orderBookListener)
        tested.removeOrderBookListener(BINANCE, currencyPair_AB, orderBookListener)
        // then
        verify(registrationListener).onLastListenerDeregistered(BINANCE)
    }

}
