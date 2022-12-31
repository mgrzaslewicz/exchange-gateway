package automate.profit.autocoin.order

import automate.profit.autocoin.SerializableToJson
import automate.profit.autocoin.api.exchange.currency.CurrencyPair
import automate.profit.autocoin.api.exchange.order.CancelOrderParams
import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.order.OrderSide


data class CancelOrderParamsDto(
    val exchangeName: String,
    val orderId: String,
    val orderSide: String,
    val currencyPair: CurrencyPairDto,
) : SerializableToJson {
    fun toCancelOrderParams() = CancelOrderParams(
        orderId = orderId,
        orderSide = OrderSide.valueOf(orderSide),
        exchangeName = ExchangeName(exchangeName),
        currencyPair = CurrencyPair.of(currencyPair.base, currencyPair.counter),
    )

    override fun appendJson(builder: StringBuilder) = builder
        .append("{")
        .append("\"exchangeName\":\"$exchangeName\",")
        .append("\"orderId\":\"$orderId\",")
        .append("\"orderSide\":\"$orderSide\",")
        .append("\"currencyPair\":")
        .append(currencyPair.toJson())
        .append("}")

}
