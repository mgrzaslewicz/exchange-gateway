package com.autocoin.exchangegateway.dto.order

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyPair

data class CurrencyPairDto(
    val base: String,
    val counter: String,
) {
    fun toCurrencyPair() = CurrencyPair.of(base, counter)
}

fun CurrencyPair.toDto() = CurrencyPairDto(
    base = this.base,
    counter = this.counter,
)
