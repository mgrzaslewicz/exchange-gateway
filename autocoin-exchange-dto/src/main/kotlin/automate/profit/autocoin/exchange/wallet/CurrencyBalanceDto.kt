package automate.profit.autocoin.exchange.wallet

import automate.profit.autocoin.SerializableToJson
import automate.profit.autocoin.api.exchange.currency.CurrencyBalance
import automate.profit.autocoin.appendMapWithNullableValues

data class CurrencyBalanceDto(
    val currencyCode: String,
    val amountAvailable: String,
    val totalAmount: String,
    val amountInOrders: String,
    val valueInOtherCurrency: Map<String, String?>? = null,
    val priceInOtherCurrency: Map<String, String?>? = null,
) : SerializableToJson {

    override fun appendJson(builder: StringBuilder) = builder
        .append("""{"currencyCode":""")
        .append(currencyCode)
        .append(""","amountAvailable":""")
        .append(amountAvailable)
        .append(""","totalAmount":""")
        .append(totalAmount)
        .append(""","amountInOrders":""")
        .append(amountInOrders)
        .append(""","valueInOtherCurrency":""")
        .appendMapWithNullableValues(valueInOtherCurrency)
        .append(""","priceInOtherCurrency":""")
        .appendMapWithNullableValues(priceInOtherCurrency)
        .append("}")

    fun toCurrencyBalance() = CurrencyBalance(
        currencyCode = currencyCode,
        amountAvailable = amountAvailable.toBigDecimal(),
        totalAmount = totalAmount.toBigDecimal(),
        amountInOrders = amountInOrders.toBigDecimal(),
    )

}

fun CurrencyBalance.toDto() = CurrencyBalanceDto(
    currencyCode = this.currencyCode,
    amountAvailable = this.amountAvailable.toPlainString(),
    totalAmount = this.totalAmount.toPlainString(),
    amountInOrders = this.amountInOrders.toPlainString(),
)

