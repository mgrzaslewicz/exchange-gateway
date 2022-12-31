package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.ExecutorService

interface OrderBookListenersVisitor {
    fun fetchOrderBooksThenNotifyListeners(exchange: SupportedExchange, currencyPairsWithListeners: Map<CurrencyPair, Set<OrderBookListener>>)
}

interface OrderBookRegistrationListener {
    fun onLastListenerDeregistered(exchange: SupportedExchange)
    fun onFirstListenerRegistered(exchange: SupportedExchange)
}

interface OrderBookListeners {
    fun addOrderBookListener(supportedExchange: SupportedExchange, currencyPair: CurrencyPair, listener: OrderBookListener): Boolean
    fun removeOrderBookListener(supportedExchange: SupportedExchange, currencyPair: CurrencyPair, listener: OrderBookListener): Boolean
    fun addOrderBookRegistrationListener(orderBookRegistrationListener: OrderBookRegistrationListener)
    fun removeOrderBookRegistrationListener(orderBookRegistrationListener: OrderBookRegistrationListener)
    fun iterateOverEachExchangeAndAllCurrencyPairs(orderBookListenersVisitor: OrderBookListenersVisitor)
}

class DefaultOrderBookListeners(private val executorService: ExecutorService) : OrderBookListeners {

    private val listenersByExchangeAndCurrencyPair = ConcurrentHashMap<SupportedExchange, ConcurrentHashMap<CurrencyPair, MutableSet<OrderBookListener>>>()
    private val orderBookRegistrationListeners = mutableListOf<OrderBookRegistrationListener>()

    override fun addOrderBookListener(exchange: SupportedExchange, currencyPair: CurrencyPair, listener: OrderBookListener): Boolean {
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
        return isListenerAdded
    }

    override fun removeOrderBookListener(exchange: SupportedExchange, currencyPair: CurrencyPair, listener: OrderBookListener): Boolean {
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

    override fun iterateOverEachExchangeAndAllCurrencyPairs(orderBookListenerVisitor: OrderBookListenersVisitor) {
        listenersByExchangeAndCurrencyPair.forEach { (exchange, currencyPairsWithListeners) ->
            executorService.submit {
                orderBookListenerVisitor.fetchOrderBooksThenNotifyListeners(exchange, currencyPairsWithListeners)
            }
        }
    }

}
