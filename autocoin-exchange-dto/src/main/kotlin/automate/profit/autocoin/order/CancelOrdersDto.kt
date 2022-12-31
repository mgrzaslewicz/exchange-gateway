package automate.profit.autocoin.order

import automate.profit.autocoin.SerializableToJson

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

