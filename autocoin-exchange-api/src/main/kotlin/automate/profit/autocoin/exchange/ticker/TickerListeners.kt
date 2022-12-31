package automate.profit.autocoin.exchange.ticker

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

interface TickerRegistrationListener {
    fun onLastListenerDeregistered(exchange: SupportedExchange)
    fun onListenerDeregistered(exchange: SupportedExchange, currencyPair: CurrencyPair)
    fun onFirstListenerRegistered(exchange: SupportedExchange)
    fun onListenerRegistered(exchange: SupportedExchange, currencyPair: CurrencyPair)
}

interface TickerListeners {
    fun addTickerListener(supportedExchange: SupportedExchange, currencyPair: CurrencyPair, listener: TickerListener): Boolean
    fun removeTickerListener(supportedExchange: SupportedExchange, currencyPair: CurrencyPair, listener: TickerListener): Boolean
    fun addTickerRegistrationListener(TickerRegistrationListener: TickerRegistrationListener)
    fun removeTickerRegistrationListener(TickerRegistrationListener: TickerRegistrationListener)
    fun getTickerListeners(supportedExchange: SupportedExchange): Map<CurrencyPair, Set<TickerListener>>
}

class DefaultTickerListeners : TickerListeners {

    private val listenersByExchangeAndCurrencyPair = ConcurrentHashMap<SupportedExchange, ConcurrentHashMap<CurrencyPair, MutableSet<TickerListener>>>()
    private val tickerRegistrationListeners = mutableListOf<TickerRegistrationListener>()

    override fun addTickerListener(exchange: SupportedExchange, currencyPair: CurrencyPair, listener: TickerListener): Boolean {
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

    override fun removeTickerListener(exchange: SupportedExchange, currencyPair: CurrencyPair, listener: TickerListener): Boolean {
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

    override fun addTickerRegistrationListener(TickerRegistrationListener: TickerRegistrationListener) {
        tickerRegistrationListeners.add(TickerRegistrationListener)
    }

    override fun removeTickerRegistrationListener(TickerRegistrationListener: TickerRegistrationListener) {
        tickerRegistrationListeners.remove(TickerRegistrationListener)
    }

    override fun getTickerListeners(supportedExchange: SupportedExchange): Map<CurrencyPair, Set<TickerListener>> {
        return listenersByExchangeAndCurrencyPair.getOrElse(supportedExchange, { mapOf() })
    }

}
