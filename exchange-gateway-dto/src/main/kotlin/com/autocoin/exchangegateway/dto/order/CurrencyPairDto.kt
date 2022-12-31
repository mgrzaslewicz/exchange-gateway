package com.autocoin.exchangegateway.dto.order

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.dto.SerializableToJson
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair as SpiCurrencyPair

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
