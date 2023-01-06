package com.autocoin.exchangegateway.dto.exchange.wallet

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyBalance
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyBalance as SpiCurrencyBalance

data class CurrencyBalanceDto(
    val currencyCode: String,
    val amountAvailable: String,
    val totalAmount: String,
    val amountInOrders: String,
    val valueInOtherCurrency: Map<String, String?>? = null,
    val priceInOtherCurrency: Map<String, String?>? = null,
) {

    fun toCurrencyBalance() = CurrencyBalance(
        currencyCode = currencyCode,
        amountAvailable = amountAvailable.toBigDecimal(),
        totalAmount = totalAmount.toBigDecimal(),
        amountInOrders = amountInOrders.toBigDecimal(),
    )

}

fun SpiCurrencyBalance.toDto() = CurrencyBalanceDto(
    currencyCode = this.currencyCode,
    amountAvailable = this.amountAvailable.toPlainString(),
    totalAmount = this.totalAmount.toPlainString(),
    amountInOrders = this.amountInOrders.toPlainString(),
)

