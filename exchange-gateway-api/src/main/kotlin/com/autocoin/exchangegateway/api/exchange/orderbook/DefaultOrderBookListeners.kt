package com.autocoin.exchangegateway.api.exchange.orderbook

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.orderbook.listener.OrderBookListener
import com.autocoin.exchangegateway.spi.exchange.orderbook.listener.OrderBookListeners
import com.autocoin.exchangegateway.spi.exchange.orderbook.listener.OrderBookRegistrationListener
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet


class DefaultOrderBookListeners : OrderBookListeners {

    private val listenersByExchangeAndCurrencyPair =
        ConcurrentHashMap<Exchange, ConcurrentHashMap<CurrencyPair, MutableSet<OrderBookListener>>>()
    private val orderBookRegistrationListeners = mutableListOf<OrderBookRegistrationListener>()

    override fun addOrderBookListener(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        listener: OrderBookListener,
    ): Boolean {
        var isFirstListenerWithGivenExchangeAdded = false
        val listenersByCurrencyPair = listenersByExchangeAndCurrencyPair.computeIfAbsent(exchange) {
            val map = ConcurrentHashMap<CurrencyPair, MutableSet<OrderBookListener>>()
            isFirstListenerWithGivenExchangeAdded = true // avoid triggering registration listener to iterate over collection that is just being changed
            map
        }
        val isListenerAdded = listenersByCurrencyPair.computeIfAbsent(currencyPair) {
            CopyOnWriteArraySet()
        }.add(listener)

        if (isFirstListenerWithGivenExchangeAdded) {
            orderBookRegistrationListeners.forEach {
                it.onFirstListenerRegistered(exchange)
            }
        }
        orderBookRegistrationListeners.forEach {
            it.onListenerRegistered(exchange, currencyPair)
        }
        return isListenerAdded
    }

    override fun removeOrderBookListener(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        listener: OrderBookListener,
    ): Boolean {
        var isLastListenerWithGivenExchangeRemoved = false
        var isRemoved = false
        val listenersByCurrencyPair = listenersByExchangeAndCurrencyPair[exchange]
        if (listenersByCurrencyPair != null) {
            val orderBookListeners = listenersByCurrencyPair[currencyPair]
            if (orderBookListeners != null && orderBookListeners.isNotEmpty()) {
                isRemoved = orderBookListeners.remove(listener)
                if (isRemoved) {
                    if (orderBookListeners.isEmpty()) {
                        listenersByCurrencyPair.remove(currencyPair)
                        if (listenersByCurrencyPair.isEmpty()) {
                            listenersByExchangeAndCurrencyPair.remove(exchange)
                            isLastListenerWithGivenExchangeRemoved = true
                        }
                    }
                }
            }
        }
        orderBookRegistrationListeners.forEach {
            it.onListenerDeregistered(exchange, currencyPair)
        }
        if (isLastListenerWithGivenExchangeRemoved) {
            orderBookRegistrationListeners.forEach {
                it.onLastListenerDeregistered(exchange)
            }
        }
        return isRemoved
    }

    override fun addOrderBookRegistrationListener(orderBookRegistrationListener: OrderBookRegistrationListener) {
        orderBookRegistrationListeners.add(orderBookRegistrationListener)
    }

    override fun removeOrderBookRegistrationListener(orderBookRegistrationListener: OrderBookRegistrationListener) {
        orderBookRegistrationListeners.remove(orderBookRegistrationListener)
    }

    override fun getOrderBookListeners(exchange: Exchange): Map<CurrencyPair, Set<OrderBookListener>> {
        return listenersByExchangeAndCurrencyPair.getOrElse(exchange) { emptyMap() }
    }

}
