package com.autocoin.exchangegateway.spi.exchange.orderbook

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide
import java.math.BigDecimal

interface OrderInOrderBook {
    val exchangeName: ExchangeName
    val side: OrderSide
    val orderedAmount: BigDecimal
    val price: BigDecimal
    val currencyPair: CurrencyPair
    val receivedAtMillis: Long
    val exchangeTimestampMillis: Long?
    fun valueInCounterCurrency(): BigDecimal = orderedAmount.multiply(price)
}
