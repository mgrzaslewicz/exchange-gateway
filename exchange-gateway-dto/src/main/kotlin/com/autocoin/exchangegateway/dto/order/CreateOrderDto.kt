package com.autocoin.exchangegateway.dto.order

import com.autocoin.exchangegateway.spi.exchange.order.OrderSide

data class CreateOrderDto(
    val exchangeName: String,
    val exchangeUserId: String,
    val orderSide: OrderSide,
    val baseCurrencyCode: String,
    val counterCurrencyCode: String,
    val price: String,
    val amount: String,
)
