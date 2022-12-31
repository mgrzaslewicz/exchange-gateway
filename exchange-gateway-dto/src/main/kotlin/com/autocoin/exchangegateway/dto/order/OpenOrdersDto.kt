package com.autocoin.exchangegateway.dto.order

import com.autocoin.exchangegateway.api.exchange.order.OpenOrders
import com.autocoin.exchangegateway.dto.SerializableToJson
import com.autocoin.exchangegateway.dto.appendNullable
import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.order.OpenOrders as SpiOpenOrders

data class OpenOrdersDto(
    val exchangeName: String,
    val exchangeUserId: String,
    val openOrders: List<OrderDto>,
    val errorMessage: String?,
) : SerializableToJson {
    fun toOpenOrders() = OpenOrders(
        exchangeName = ExchangeName(exchangeName),
        exchangeUserId = exchangeUserId,
        openOrders = openOrders.map { it.toOrder() },
        errorMessage = errorMessage,
    )

    override fun appendJson(builder: StringBuilder) = builder
        .append("{")
        .append("\"exchangeName\":\"$exchangeName\",")
        .append("\"exchangeUserId\":\"$exchangeUserId\",")
        .append("\"openOrders\":[")
        .append(openOrders.joinToString(",") { it.toJson() })
        .append("],")
        .append("\"errorMessage\":")
        .appendNullable(errorMessage)
        .append("}")
}

fun SpiOpenOrders.toDto() = OpenOrdersDto(
    exchangeName = this.exchangeName.value,
    exchangeUserId = this.exchangeUserId,
    openOrders = this.openOrders.map { it.toDto() },
    errorMessage = this.errorMessage,
)

