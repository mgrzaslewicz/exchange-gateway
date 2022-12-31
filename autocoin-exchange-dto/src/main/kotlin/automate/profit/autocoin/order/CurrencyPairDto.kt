package automate.profit.autocoin.order

import automate.profit.autocoin.SerializableToJson
import automate.profit.autocoin.api.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair as SpiCurrencyPair

data class CurrencyPairDto(
    val base: String,
    val counter: String,
) : SerializableToJson {
    fun toCurrencyPair(): SpiCurrencyPair = CurrencyPair.of(base, counter)

    override fun appendJson(builder: StringBuilder) = builder
        .append("{")
        .append("\"base\":\"$base\",")
        .append("\"counter\":\"$counter\"")
        .append("}")
}

fun CurrencyPair.toDto() = CurrencyPairDto(
    base = this.base,
    counter = this.counter,
)
