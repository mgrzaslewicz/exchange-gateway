package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.SupportedExchange

interface OrderBookListenerRegistrarProvider {
    fun createOrderBookListenerRegistrar(exchangeName: SupportedExchange): OrderBookListenerRegistrar
}