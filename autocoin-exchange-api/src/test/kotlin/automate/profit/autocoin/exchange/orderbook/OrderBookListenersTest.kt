package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.SupportedExchange.BINANCE
import automate.profit.autocoin.exchange.SupportedExchange.BITTREX
import automate.profit.autocoin.exchange.currency.CurrencyPair
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class OrderBookListenersTest {
    private lateinit var tested: OrderBookListeners
    private val currencyPair_AB = CurrencyPair.of("A/B")
    private val currencyPair_CD = CurrencyPair.of("C/D")
    private val orderBookListener = mock<OrderBookListener>()

    @BeforeEach
    fun setup() {
        tested = DefaultOrderBookListeners()
    }

    @Test
    fun shouldAddOrderBookListenersForDifferentExchanges() {
        // when
        tested.addOrderBookListener(BITTREX, currencyPair_AB, orderBookListener)
        tested.addOrderBookListener(BINANCE, currencyPair_CD, orderBookListener)
        // then
        assertThat(tested.getOrderBookListeners(BITTREX)).isEqualTo(mapOf(
                currencyPair_AB to setOf(orderBookListener)
        ))
        assertThat(tested.getOrderBookListeners(BINANCE)).isEqualTo(mapOf(
                currencyPair_CD to setOf(orderBookListener)
        ))
    }

    @Test
    fun shouldAddOrderBookListenersForTheSameExchanges() {
        // when
        tested.addOrderBookListener(BITTREX, currencyPair_AB, orderBookListener)
        tested.addOrderBookListener(BITTREX, currencyPair_CD, orderBookListener)
        // then
        assertThat(tested.getOrderBookListeners(BITTREX)).isEqualTo(mapOf(
                currencyPair_AB to setOf(orderBookListener),
                currencyPair_CD to setOf(orderBookListener)
        ))
    }

    @Test
    fun shouldRemoveOrderBookListeners() {
        // when
        tested.addOrderBookListener(BITTREX, currencyPair_AB, orderBookListener)
        tested.addOrderBookListener(BINANCE, currencyPair_CD, orderBookListener)
        tested.removeOrderBookListener(BITTREX, currencyPair_AB, orderBookListener)
        // then
        assertThat(tested.getOrderBookListeners(BITTREX)).isEmpty()
        assertThat(tested.getOrderBookListeners(BINANCE)).isEqualTo(mapOf(
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
