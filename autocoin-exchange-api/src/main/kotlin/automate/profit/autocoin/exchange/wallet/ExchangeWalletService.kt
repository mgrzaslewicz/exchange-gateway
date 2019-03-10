package automate.profit.autocoin.exchange.wallet

import automate.profit.autocoin.exchange.currency.CurrencyBalance

interface ExchangeWalletService {
    fun getAvailableAmountOfCurrency(exchangeName: String, exchangeUserId: String, currencyCode: String) = getCurrencyBalance(exchangeName, exchangeUserId, currencyCode).available
    fun getCurrencyBalance(exchangeName: String, exchangeUserId: String, currencyCode: String): CurrencyBalance
}

