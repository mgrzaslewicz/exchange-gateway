package com.autocoin.exchangegateway.dto.order

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.api.exchange.orderbook.OrderInOrderBook
import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderInOrderBook as SpiOrderInOrderBook

data class OrderInOrderBookDto(
    val exchangeName: String,
    val side: String,
    val orderedAmount: String,
    val price: String,
    val baseCurrency: String,
    val counterCurrency: String,
    val receivedAtMillis: Long,
    val exchangeTimestampMillis: Long?,
) {
    fun toOrderInOrderBook() = OrderInOrderBook(
        exchangeName = ExchangeName(exchangeName),
        side = OrderSide.valueOf(side),
        orderedAmount = orderedAmount.toBigDecimal(),
        price = price.toBigDecimal(),
        currencyPair = CurrencyPair.of(baseCurrency, counterCurrency),
        receivedAtMillis = receivedAtMillis,
        exchangeTimestampMillis = exchangeTimestampMillis,
    )

}

fun SpiOrderInOrderBook.toDto() = OrderInOrderBookDto(
    exchangeName = exchangeName.value,
    side = side.name,
    orderedAmount = orderedAmount.toPlainString(),
    price = price.toPlainString(),
    baseCurrency = currencyPair.base,
    counterCurrency = currencyPair.counter,
    receivedAtMillis = receivedAtMillis,
    exchangeTimestampMillis = exchangeTimestampMillis,
)

