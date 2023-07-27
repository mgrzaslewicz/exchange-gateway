package com.autocoin.exchangegateway.api.exchange.ticker

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.ticker.listener.TickerListener
import com.autocoin.exchangegateway.spi.exchange.ticker.listener.TickerListeners
import com.autocoin.exchangegateway.spi.exchange.ticker.listener.TickerRegistrationListener
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet


class DefaultTickerListeners : TickerListeners {

    private val listenersByExchangeAndCurrencyPair =
        ConcurrentHashMap<Exchange, ConcurrentHashMap<CurrencyPair, MutableSet<TickerListener>>>()
    private val tickerRegistrationListeners = mutableListOf<TickerRegistrationListener>()

    override fun addTickerListener(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        listener: TickerListener,
    ): Boolean {
        var isFirstListenerWithGivenExchangeAdded = false
        val listenersByCurrencyPair = listenersByExchangeAndCurrencyPair.computeIfAbsent(exchange) {
            val map = ConcurrentHashMap<CurrencyPair, MutableSet<TickerListener>>()
            isFirstListenerWithGivenExchangeAdded = true // avoid triggering registration listener to iterate over collection that is just being changed
            map
        }
        val isListenerAdded = listenersByCurrencyPair.computeIfAbsent(currencyPair) {
            CopyOnWriteArraySet()
        }.add(listener)

        if (isFirstListenerWithGivenExchangeAdded) {
            tickerRegistrationListeners.forEach {
                it.onFirstListenerRegistered(exchange)
            }
        }
        tickerRegistrationListeners.forEach {
            it.onListenerRegistered(exchange, currencyPair)
        }
        return isListenerAdded
    }

    override fun removeTickerListener(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        listener: TickerListener,
    ): Boolean {
        var isLastListenerWithGivenExchangeRemoved = false
        var isRemoved = false
        val listenersByCurrencyPair = listenersByExchangeAndCurrencyPair[exchange]
        if (listenersByCurrencyPair != null) {
            val tickerListeners = listenersByCurrencyPair[currencyPair]
            if (tickerListeners != null && tickerListeners.isNotEmpty()) {
                isRemoved = tickerListeners.remove(listener)
                if (isRemoved) {
                    if (tickerListeners.isEmpty()) {
                        listenersByCurrencyPair.remove(currencyPair)
                        if (listenersByCurrencyPair.isEmpty()) {
                            listenersByExchangeAndCurrencyPair.remove(exchange)
                            isLastListenerWithGivenExchangeRemoved = true
                        }
                    }
                }
            }
        }
        tickerRegistrationListeners.forEach {
            it.onListenerDeregistered(exchange, currencyPair)
        }
        if (isLastListenerWithGivenExchangeRemoved) {
            tickerRegistrationListeners.forEach {
                it.onLastListenerDeregistered(exchange)
            }
        }
        return isRemoved
    }

    override fun addTickerRegistrationListener(tickerRegistrationListener: TickerRegistrationListener) {
        tickerRegistrationListeners.add(tickerRegistrationListener)
    }

    override fun removeTickerRegistrationListener(tickerRegistrationListener: TickerRegistrationListener) {
        tickerRegistrationListeners.remove(tickerRegistrationListener)
    }

    override fun getTickerListeners(exchange: Exchange): Map<CurrencyPair, Set<TickerListener>> {
        return listenersByExchangeAndCurrencyPair.getOrElse(exchange) { mapOf() }
    }

}
