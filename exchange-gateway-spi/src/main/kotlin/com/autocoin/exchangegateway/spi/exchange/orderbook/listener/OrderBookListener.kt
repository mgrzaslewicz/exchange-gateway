package com.autocoin.exchangegateway.spi.exchange.orderbook.listener

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderBook

interface OrderBookListener {

    fun onOrderBook(
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
        orderBook: OrderBook,
    )

    /**
     * There was no orderBook change on exchange but time has passed
     * @param orderBook might be the same that was already fetched from exchange or none
     */
    fun onNoNewOrderBook(
        exchange: ExchangeName,
        currencyPair: CurrencyPair,
        orderBook: OrderBook?,
    ) {
    }

}
