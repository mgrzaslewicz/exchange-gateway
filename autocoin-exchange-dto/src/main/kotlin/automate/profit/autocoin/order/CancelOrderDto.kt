package automate.profit.autocoin.order

import automate.profit.autocoin.SerializableToJson

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

