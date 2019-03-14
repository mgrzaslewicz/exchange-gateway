package automate.profit.autocoin.exchange.wallet

import automate.profit.autocoin.exchange.currency.CurrencyBalance

data class ExchangeSingleCurrencyBalanceDto(
        val exchangeName: String,
        val currencyBalance: CurrencyBalanceDto?,
        val errorMessage: String?
) {
    fun toCurrencyBalance() = CurrencyBalance(
            currencyCode = currencyBalance!!.currencyCode,
            total = currencyBalance.total.toBigDecimal(),
            available = currencyBalance.available.toBigDecimal(),
            frozen = currencyBalance.frozen.toBigDecimal()
    )
}
