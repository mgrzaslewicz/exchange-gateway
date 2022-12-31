package com.autocoin.exchangegateway.dto.order

import com.autocoin.exchangegateway.dto.SerializableToJson


data class CancelOrdersDto(
    val orders: List<CancelOrderDto>,
) : SerializableToJson {

    override fun appendJson(builder: StringBuilder) = builder
        .append("{")
        .append("\"orders\":[")
        .append(orders.joinToString(",") { it.toJson() })
        .append("]")
        .append("}")

}

