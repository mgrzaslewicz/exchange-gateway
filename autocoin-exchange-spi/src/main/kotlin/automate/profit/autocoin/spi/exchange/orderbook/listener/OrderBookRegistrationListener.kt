package automate.profit.autocoin.spi.exchange.orderbook.listener

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair

interface OrderBookRegistrationListener {
    fun onLastListenerDeregistered(exchangeName: ExchangeName)
    fun onListenerDeregistered(exchangeName: ExchangeName, currencyPair: CurrencyPair)
    fun onFirstListenerRegistered(exchangeName: ExchangeName)
    fun onListenerRegistered(exchangeName: ExchangeName, currencyPair: CurrencyPair)
}
