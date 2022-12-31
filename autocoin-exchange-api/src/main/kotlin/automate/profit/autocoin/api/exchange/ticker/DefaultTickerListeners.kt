package automate.profit.autocoin.api.exchange.ticker

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.ticker.listener.TickerListener
import automate.profit.autocoin.spi.exchange.ticker.listener.TickerListeners
import automate.profit.autocoin.spi.exchange.ticker.listener.TickerRegistrationListener
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet


class DefaultTickerListeners : TickerListeners {

    private val listenersByExchangeAndCurrencyPair = ConcurrentHashMap<ExchangeName, ConcurrentHashMap<CurrencyPair, MutableSet<TickerListener>>>()
    private val tickerRegistrationListeners = mutableListOf<TickerRegistrationListener>()

    override fun addTickerListener(
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
        listener: TickerListener,
    ): Boolean {
        var isFirstListenerWithGivenExchangeAdded = false
        val listenersByCurrencyPair = listenersByExchangeAndCurrencyPair.computeIfAbsent(exchangeName) {
            val map = ConcurrentHashMap<CurrencyPair, MutableSet<TickerListener>>()
            isFirstListenerWithGivenExchangeAdded = true // avoid triggering registration listener to iterate over collection that is just being changed
            map
        }
        val isListenerAdded = listenersByCurrencyPair.computeIfAbsent(currencyPair) {
            CopyOnWriteArraySet()
        }.add(listener)

        if (isFirstListenerWithGivenExchangeAdded) {
            tickerRegistrationListeners.forEach {
                it.onFirstListenerRegistered(exchangeName)
            }
        }
        tickerRegistrationListeners.forEach {
            it.onListenerRegistered(exchangeName, currencyPair)
        }
        return isListenerAdded
    }

    override fun removeTickerListener(
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
        listener: TickerListener,
    ): Boolean {
        var isLastListenerWithGivenExchangeRemoved = false
        var isRemoved = false
        val listenersByCurrencyPair = listenersByExchangeAndCurrencyPair[exchangeName]
        if (listenersByCurrencyPair != null) {
            val tickerListeners = listenersByCurrencyPair[currencyPair]
            if (tickerListeners != null && tickerListeners.isNotEmpty()) {
                isRemoved = tickerListeners.remove(listener)
                if (isRemoved) {
                    if (tickerListeners.isEmpty()) {
                        listenersByCurrencyPair.remove(currencyPair)
                        if (listenersByCurrencyPair.isEmpty()) {
                            listenersByExchangeAndCurrencyPair.remove(exchangeName)
                            isLastListenerWithGivenExchangeRemoved = true
                        }
                    }
                }
            }
        }
        tickerRegistrationListeners.forEach {
            it.onListenerDeregistered(exchangeName, currencyPair)
        }
        if (isLastListenerWithGivenExchangeRemoved) {
            tickerRegistrationListeners.forEach {
                it.onLastListenerDeregistered(exchangeName)
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

    override fun getTickerListeners(exchangeName: ExchangeName): Map<CurrencyPair, Set<TickerListener>> {
        return listenersByExchangeAndCurrencyPair.getOrElse(exchangeName) { mapOf() }
    }

}
