package automate.profit.autocoin.exchange.ticker

import automate.profit.autocoin.exchange.SupportedExchange

interface TickerListenerRegistrarProvider {
    fun createTickerListenerRegistrar(exchangeName: SupportedExchange): TickerListenerRegistrar
}
