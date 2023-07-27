package com.autocoin.exchangegateway.dto.order

import com.autocoin.exchangegateway.api.exchange.order.OpenOrders
import com.autocoin.exchangegateway.spi.exchange.ExchangeProvider
import com.autocoin.exchangegateway.spi.exchange.order.OpenOrders as SpiOpenOrders

data class OpenOrdersDto(
    val exchangeUserId: String,
    val openOrders: List<OrderDto>,
    val errorMessage: String?,
) {
    fun toOpenOrders(exchangeProvider: ExchangeProvider) = OpenOrders(
        exchangeUserId = exchangeUserId,
        openOrders = openOrders.map { it.toOrder(exchangeProvider) },
        errorMessage = errorMessage,
    )

}

fun SpiOpenOrders.toDto() = OpenOrdersDto(
    exchangeUserId = this.exchangeUserId,
    openOrders = this.openOrders.map { it.toDto() },
    errorMessage = this.errorMessage,
)

