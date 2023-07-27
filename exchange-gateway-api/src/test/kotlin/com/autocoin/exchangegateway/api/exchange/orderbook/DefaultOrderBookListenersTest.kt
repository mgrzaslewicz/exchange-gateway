package com.autocoin.exchangegateway.api.exchange.orderbook

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.orderbook.listener.OrderBookListener
import com.autocoin.exchangegateway.spi.exchange.orderbook.listener.OrderBookListeners
import com.autocoin.exchangegateway.spi.exchange.orderbook.listener.OrderBookRegistrationListener
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
    private val currencyPairAB = CurrencyPair.of("A/B")
    private val currencyPairCD = CurrencyPair.of("C/D")
    private val orderBookListener = mock<OrderBookListener>()
    private val exchange1 = object : Exchange {
        override val exchangeName = "exchange1"
    }
    private val exchange2 = object : Exchange {
        override val exchangeName = "exchange2"
    }

    @BeforeEach
    fun setup() {
        tested = DefaultOrderBookListeners()
    }

    @Test
    fun shouldAddOrderBookListenersForDifferentExchanges() {
        // when
        tested.addOrderBookListener(exchange1, currencyPairAB, orderBookListener)
        tested.addOrderBookListener(exchange2, currencyPairCD, orderBookListener)
        // then
        assertThat(tested.getOrderBookListeners(exchange1)).isEqualTo(
            mapOf(
                currencyPairAB to setOf(orderBookListener),
            ),
        )
        assertThat(tested.getOrderBookListeners(exchange2)).isEqualTo(
            mapOf(
                currencyPairCD to setOf(orderBookListener),
            ),
        )
    }

    @Test
    fun shouldAddOrderBookListenersForTheSameExchanges() {
        // when
        tested.addOrderBookListener(exchange1, currencyPairAB, orderBookListener)
        tested.addOrderBookListener(exchange1, currencyPairCD, orderBookListener)
        // then
        assertThat(tested.getOrderBookListeners(exchange1)).isEqualTo(
            mapOf(
                currencyPairAB to setOf(orderBookListener),
                currencyPairCD to setOf(orderBookListener),
            ),
        )
    }

    @Test
    fun shouldRemoveOrderBookListeners() {
        // when
        tested.addOrderBookListener(exchange1, currencyPairAB, orderBookListener)
        tested.addOrderBookListener(exchange2, currencyPairCD, orderBookListener)
        tested.removeOrderBookListener(exchange1, currencyPairAB, orderBookListener)
        // then
        assertThat(tested.getOrderBookListeners(exchange1)).isEmpty()
        assertThat(tested.getOrderBookListeners(exchange2)).isEqualTo(
            mapOf(
                currencyPairCD to setOf(orderBookListener),
            ),
        )
    }

    @Test
    fun shouldNotifyRegistrationListenerAboutFirstExchangeListenerAdded() {
        // given
        val registrationListener = mock<OrderBookRegistrationListener>()
        tested.addOrderBookRegistrationListener(registrationListener)
        // when
        tested.addOrderBookListener(exchange2, currencyPairAB, orderBookListener)
        // then
        verify(registrationListener).onFirstListenerRegistered(exchange2)
    }

    @Test
    fun shouldNotifyRegistrationListenerAboutLastExchangeListenerRemoved() {
        // given
        val registrationListener = mock<OrderBookRegistrationListener>()
        tested.addOrderBookRegistrationListener(registrationListener)
        // when
        tested.addOrderBookListener(exchange2, currencyPairAB, orderBookListener)
        tested.removeOrderBookListener(exchange2, currencyPairAB, orderBookListener)
        // then
        verify(registrationListener).onLastListenerDeregistered(exchange2)

    }

}
