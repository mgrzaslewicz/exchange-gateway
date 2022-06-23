package automate.profit.autocoin.exchange.wallet

import automate.profit.autocoin.exchange.currency.ExchangeCurrencyBalance

data class ExchangeSingleCurrencyBalanceDto(
    val exchangeName: String,
    val currencyBalance: ExchangeCurrencyBalanceDto?,
    val errorMessage: String?
) {
    fun toCurrencyBalance() = ExchangeCurrencyBalance(
        currencyCode = currencyBalance!!.currencyCode,
        totalAmount = currencyBalance.totalAmount.toBigDecimal(),
        amountAvailable = currencyBalance.amountAvailable.toBigDecimal(),
        amountInOrders = currencyBalance.amountInOrders.toBigDecimal(),
    )
}
