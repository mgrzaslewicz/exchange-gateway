package com.autocoin.exchangegateway.spi.exchange.order

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import java.math.BigDecimal

enum class OrderSide {
    ASK_SELL,
    BID_BUY
}

enum class OrderStatus {
    NEW,
    FILLED,
    PARTIALLY_FILLED,
    PARTIALLY_CANCELED,
    CANCELED,
    NOT_AVAILABLE
}

interface Order {
    val exchange: Exchange
    val exchangeOrderId: String
    val side: OrderSide
    val orderedAmount: BigDecimal
    val filledAmount: BigDecimal?
    val price: BigDecimal
    val currencyPair: CurrencyPair
    val status: OrderStatus
    val receivedAtMillis: Long
    val exchangeTimestampMillis: Long?
}
