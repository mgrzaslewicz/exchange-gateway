package com.autocoin.exchangegateway.spi.exchange.orderbook.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderBook

class DelegateOrderBookServiceGateway(
    private val orderBookServiceGateways: Map<Exchange, OrderBookServiceGateway>,
) : OrderBookServiceGateway {
    override fun getOrderBook(
        exchange: Exchange,
        currencyPair: CurrencyPair,
    ): OrderBook {
        return orderBookServiceGateways.getValue(exchange).getOrderBook(
            exchange = exchange,
            currencyPair = currencyPair,
        )
    }
}
