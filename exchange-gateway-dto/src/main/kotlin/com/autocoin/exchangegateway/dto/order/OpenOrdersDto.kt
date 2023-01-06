package com.autocoin.exchangegateway.dto.order

import com.autocoin.exchangegateway.api.exchange.order.OpenOrders
import com.autocoin.exchangegateway.dto.SerializableToJson
import com.autocoin.exchangegateway.dto.appendNullable
import com.autocoin.exchangegateway.spi.exchange.order.OpenOrders as SpiOpenOrders

data class OpenOrdersDto(
    val exchangeUserId: String,
    val openOrders: List<OrderDto>,
    val errorMessage: String?,
) : SerializableToJson {
    fun toOpenOrders() = OpenOrders(
        exchangeUserId = exchangeUserId,
        openOrders = openOrders.map { it.toOrder() },
        errorMessage = errorMessage,
    )

    override fun appendJson(builder: StringBuilder) = builder
        .append("{")
        .append("\"exchangeUserId\":\"$exchangeUserId\",")
        .append("\"openOrders\":[")
        .append(openOrders.joinToString(",") { it.toJson() })
        .append("],")
        .append("\"errorMessage\":")
        .appendNullable(errorMessage)
        .append("}")
}

fun SpiOpenOrders.toDto() = OpenOrdersDto(
    exchangeUserId = this.exchangeUserId,
    openOrders = this.openOrders.map { it.toDto() },
    errorMessage = this.errorMessage,
)

