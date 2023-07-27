package com.autocoin.exchangegateway.spi.exchange.orderbook

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide
import java.math.BigDecimal

interface OrderInOrderBook {
    val exchange: Exchange
    val side: OrderSide
    val orderedAmount: BigDecimal
    val price: BigDecimal
    val currencyPair: CurrencyPair
    val receivedAtMillis: Long
    val exchangeTimestampMillis: Long?
    fun valueInCounterCurrency(): BigDecimal = orderedAmount.multiply(price)
}
