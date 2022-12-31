package automate.profit.autocoin.exchange.wallet

import automate.profit.autocoin.api.exchange.currency.CurrencyBalance


data class SingleCurrencyBalanceDto(
    val exchangeName: String,
    val currencyBalance: CurrencyBalanceDto?,
    val errorMessage: String?,
) {
    fun toCurrencyBalance() = CurrencyBalance(
        currencyCode = currencyBalance!!.currencyCode,
        totalAmount = currencyBalance.totalAmount.toBigDecimal(),
        amountAvailable = currencyBalance.amountAvailable.toBigDecimal(),
        amountInOrders = currencyBalance.amountInOrders.toBigDecimal(),
    )
}
