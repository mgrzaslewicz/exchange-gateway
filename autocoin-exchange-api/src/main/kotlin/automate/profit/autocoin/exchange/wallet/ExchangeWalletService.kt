package automate.profit.autocoin.exchange.wallet

import automate.profit.autocoin.exchange.currency.ExchangeCurrencyBalance

data class ExchangeWithErrorMessage(
    val exchangeName: String,
    val errorMessage: String?
) {
    fun hasNoError() = errorMessage == null
}

interface ExchangeWalletService {
    fun getAvailableAmountOfCurrency(exchangeName: String, exchangeUserId: String, currencyCode: String) = getCurrencyBalance(exchangeName, exchangeUserId, currencyCode).amountAvailable
    fun getCurrencyBalance(exchangeName: String, exchangeUserId: String, currencyCode: String): ExchangeCurrencyBalance
    fun getCurrencyBalances(exchangeName: String, exchangeUserId: String): List<ExchangeCurrencyBalance>
    fun getCurrencyBalancesForEveryExchange(exchangeUserId: String): Map<ExchangeWithErrorMessage, List<ExchangeCurrencyBalance>>
}

