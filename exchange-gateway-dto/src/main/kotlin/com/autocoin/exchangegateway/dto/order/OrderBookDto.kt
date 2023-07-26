package com.autocoin.exchangegateway.dto.order

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.api.exchange.orderbook.OrderBook
import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderBook as SpiOrderBook


data class OrderBookDto(
    val exchangeName: String,
    /**
     * A/B format
     */
    val currencyPair: String,
    val buyOrders: List<OrderInOrderBookDto> = emptyList(),
    val sellOrders: List<OrderInOrderBookDto> = emptyList(),

    val receivedAtMillis: Long,
    val exchangeTimestampMillis: Long?,

    val errorMessage: String? = null,
) {
    fun toOrderBook(): SpiOrderBook = OrderBook(
        exchangeName = ExchangeName(exchangeName),
        buyOrders = buyOrders.map { it.toOrderInOrderBook() },
        sellOrders = sellOrders.map { it.toOrderInOrderBook() },
        receivedAtMillis = receivedAtMillis,
        exchangeTimestampMillis = exchangeTimestampMillis,
        currencyPair = CurrencyPair.of(currencyPair),
    )

}

fun SpiOrderBook.toDto() =
    OrderBookDto(
        exchangeName = exchangeName.value,
        currencyPair = currencyPair.toString(),
        buyOrders = buyOrders
            .sortedByDescending { it.price }
            .map { it.toDto() },
        sellOrders = sellOrders
            .sortedBy { it.price }
            .map { it.toDto() },
        receivedAtMillis = receivedAtMillis,
        exchangeTimestampMillis = exchangeTimestampMillis,
    )

