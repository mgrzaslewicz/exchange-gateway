package automate.profit.autocoin.spi.exchange.wallet.service

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier
import automate.profit.autocoin.spi.exchange.currency.CurrencyBalance

interface WalletService<T> {
    val exchangeName: ExchangeName

    fun getCurrencyBalance(
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
    ): CurrencyBalance

    fun getCurrencyBalances(apiKey: ApiKeySupplier<T>): List<CurrencyBalance>

}

