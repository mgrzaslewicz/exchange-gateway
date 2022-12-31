package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.api.exchange.currency.CurrencyPair
import automate.profit.autocoin.api.exchange.orderbook.DefaultOrderBookListeners
import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.orderbook.listener.OrderBookListener
import automate.profit.autocoin.spi.exchange.orderbook.listener.OrderBookListeners
import automate.profit.autocoin.spi.exchange.orderbook.listener.OrderBookRegistrationListener
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class DefaultOrderBookListenersTest {
    private lateinit var tested: OrderBookListeners
    private val currencyPair_AB = CurrencyPair.of("A/B")
    private val currencyPair_CD = CurrencyPair.of("C/D")
    private val orderBookListener = mock<OrderBookListener>()
    private val bittrex = ExchangeName("bittrex")
    private val binance = ExchangeName("binance")

    @BeforeEach
    fun setup() {
        tested = DefaultOrderBookListeners()
    }

    @Test
    fun shouldAddOrderBookListenersForDifferentExchanges() {
        // when
        tested.addOrderBookListener(bittrex, currencyPair_AB, orderBookListener)
        tested.addOrderBookListener(binance, currencyPair_CD, orderBookListener)
        // then
        assertThat(tested.getOrderBookListeners(bittrex)).isEqualTo(
            mapOf(
                currencyPair_AB to setOf(orderBookListener)
            )
        )
        assertThat(tested.getOrderBookListeners(binance)).isEqualTo(
            mapOf(
                currencyPair_CD to setOf(orderBookListener)
            )
        )
    }

    @Test
    fun shouldAddOrderBookListenersForTheSameExchanges() {
        // when
        tested.addOrderBookListener(bittrex, currencyPair_AB, orderBookListener)
        tested.addOrderBookListener(bittrex, currencyPair_CD, orderBookListener)
        // then
        assertThat(tested.getOrderBookListeners(bittrex)).isEqualTo(
            mapOf(
                currencyPair_AB to setOf(orderBookListener),
                currencyPair_CD to setOf(orderBookListener)
            )
        )
    }

    @Test
    fun shouldRemoveOrderBookListeners() {
        // when
        tested.addOrderBookListener(bittrex, currencyPair_AB, orderBookListener)
        tested.addOrderBookListener(binance, currencyPair_CD, orderBookListener)
        tested.removeOrderBookListener(bittrex, currencyPair_AB, orderBookListener)
        // then
        assertThat(tested.getOrderBookListeners(bittrex)).isEmpty()
        assertThat(tested.getOrderBookListeners(binance)).isEqualTo(
            mapOf(
                currencyPair_CD to setOf(orderBookListener)
            )
        )
    }

    @Test
    fun shouldNotifyRegistrationListenerAboutFirstExchangeListenerAdded() {
        // given
        val registrationListener = mock<OrderBookRegistrationListener>()
        tested.addOrderBookRegistrationListener(registrationListener)
        // when
        tested.addOrderBookListener(binance, currencyPair_AB, orderBookListener)
        // then
        verify(registrationListener).onFirstListenerRegistered(binance)
    }

    @Test
    fun shouldNotifyRegistrationListenerAboutLastExchangeListenerRemoved() {
        // given
        val registrationListener = mock<OrderBookRegistrationListener>()
        tested.addOrderBookRegistrationListener(registrationListener)
        // when
        tested.addOrderBookListener(binance, currencyPair_AB, orderBookListener)
        tested.removeOrderBookListener(binance, currencyPair_AB, orderBookListener)
        // then
        verify(registrationListener).onLastListenerDeregistered(binance)

    }

}
