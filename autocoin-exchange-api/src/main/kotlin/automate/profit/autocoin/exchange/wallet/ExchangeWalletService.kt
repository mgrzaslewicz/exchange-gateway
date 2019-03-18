package automate.profit.autocoin.exchange.wallet

import automate.profit.autocoin.exchange.currency.CurrencyBalance

data class ExchangeWithErrorMessage(
        val exchangeName: String,
        val errorMessage: String?
) {
    fun hasNoError() = errorMessage == null
}

interface ExchangeWalletService {
    fun getAvailableAmountOfCurrency(exchangeName: String, exchangeUserId: String, currencyCode: String) = getCurrencyBalance(exchangeName, exchangeUserId, currencyCode).available
    fun getCurrencyBalance(exchangeName: String, exchangeUserId: String, currencyCode: String): CurrencyBalance
    fun getCurrencyBalances(exchangeName: String, exchangeUserId: String): List<CurrencyBalance>
    fun getCurrencyBalancesForEveryExchange(exchangeUserId: String): Map<ExchangeWithErrorMessage, List<CurrencyBalance>>
}

