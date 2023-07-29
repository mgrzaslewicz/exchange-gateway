package com.autocoin.exchangegateway.spi.exchange.orderbook.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderBook

class DelegateOrderBookServiceGateway<T>(
    private val orderBookServiceGateways: Map<Exchange, OrderBookServiceGateway<T>>,
) : OrderBookServiceGateway<T> {
    override fun getOrderBook(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        apiKey: ApiKeySupplier<T>,
    ): OrderBook {
        return orderBookServiceGateways.getValue(exchange).getOrderBook(
            exchange = exchange,
            currencyPair = currencyPair,
            apiKey = apiKey,
        )
    }
}
