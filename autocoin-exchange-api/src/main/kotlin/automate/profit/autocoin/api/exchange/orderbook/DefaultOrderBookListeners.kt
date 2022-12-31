package automate.profit.autocoin.api.exchange.orderbook

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.orderbook.listener.OrderBookListener
import automate.profit.autocoin.spi.exchange.orderbook.listener.OrderBookListeners
import automate.profit.autocoin.spi.exchange.orderbook.listener.OrderBookRegistrationListener
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet


class DefaultOrderBookListeners : OrderBookListeners {

    private val listenersByExchangeAndCurrencyPair = ConcurrentHashMap<ExchangeName, ConcurrentHashMap<CurrencyPair, MutableSet<OrderBookListener>>>()
    private val orderBookRegistrationListeners = mutableListOf<OrderBookRegistrationListener>()

    override fun addOrderBookListener(exchangeName: ExchangeName, currencyPair: CurrencyPair, listener: OrderBookListener): Boolean {
        var isFirstListenerWithGivenExchangeAdded = false
        val listenersByCurrencyPair = listenersByExchangeAndCurrencyPair.computeIfAbsent(exchangeName) {
            val map = ConcurrentHashMap<CurrencyPair, MutableSet<OrderBookListener>>()
            isFirstListenerWithGivenExchangeAdded = true // avoid triggering registration listener to iterate over collection that is just being changed
            map
        }
        val isListenerAdded = listenersByCurrencyPair.computeIfAbsent(currencyPair) {
            CopyOnWriteArraySet()
        }.add(listener)

        if (isFirstListenerWithGivenExchangeAdded) {
            orderBookRegistrationListeners.forEach {
                it.onFirstListenerRegistered(exchangeName)
            }
        }
        orderBookRegistrationListeners.forEach {
            it.onListenerRegistered(exchangeName, currencyPair)
        }
        return isListenerAdded
    }

    override fun removeOrderBookListener(exchangeName: ExchangeName, currencyPair: CurrencyPair, listener: OrderBookListener): Boolean {
        var isLastListenerWithGivenExchangeRemoved = false
        var isRemoved = false
        val listenersByCurrencyPair = listenersByExchangeAndCurrencyPair[exchangeName]
        if (listenersByCurrencyPair != null) {
            val orderBookListeners = listenersByCurrencyPair[currencyPair]
            if (orderBookListeners != null && orderBookListeners.isNotEmpty()) {
                isRemoved = orderBookListeners.remove(listener)
                if (isRemoved) {
                    if (orderBookListeners.isEmpty()) {
                        listenersByCurrencyPair.remove(currencyPair)
                        if (listenersByCurrencyPair.isEmpty()) {
                            listenersByExchangeAndCurrencyPair.remove(exchangeName)
                            isLastListenerWithGivenExchangeRemoved = true
                        }
                    }
                }
            }
        }
        orderBookRegistrationListeners.forEach {
            it.onListenerDeregistered(exchangeName, currencyPair)
        }
        if (isLastListenerWithGivenExchangeRemoved) {
            orderBookRegistrationListeners.forEach {
                it.onLastListenerDeregistered(exchangeName)
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

    override fun getOrderBookListeners(exchangeName: ExchangeName): Map<CurrencyPair, Set<OrderBookListener>> {
        return listenersByExchangeAndCurrencyPair.getOrElse(exchangeName) { emptyMap() }
    }

}
