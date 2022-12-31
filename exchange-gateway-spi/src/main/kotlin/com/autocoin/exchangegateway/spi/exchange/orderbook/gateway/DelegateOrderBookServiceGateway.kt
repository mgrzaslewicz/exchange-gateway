package com.autocoin.exchangegateway.spi.exchange.orderbook.gateway

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderBook

class DelegateOrderBookServiceGateway(
    private val orderBookServiceGateways: Map<ExchangeName, OrderBookServiceGateway>,
) : OrderBookServiceGateway {
    override fun getOrderBook(
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
    ): OrderBook {
        return orderBookServiceGateways.getValue(exchangeName).getOrderBook(
            exchangeName = exchangeName,
            currencyPair = currencyPair,
        )
    }
}
