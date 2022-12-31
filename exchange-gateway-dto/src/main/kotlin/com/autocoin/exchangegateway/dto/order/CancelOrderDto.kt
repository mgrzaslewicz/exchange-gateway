package com.autocoin.exchangegateway.dto.order

import com.autocoin.exchangegateway.dto.SerializableToJson


data class CancelOrderDto(
    val orderId: String,
    val success: Boolean,
) : SerializableToJson {

    override fun appendJson(builder: StringBuilder) = builder
        .append("""{"orderId":""")
        .append(orderId)
        .append(""","success":""")
        .append(success)
        .append("}")

}

