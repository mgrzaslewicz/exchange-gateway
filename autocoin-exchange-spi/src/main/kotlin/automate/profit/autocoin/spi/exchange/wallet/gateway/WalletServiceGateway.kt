package automate.profit.autocoin.spi.exchange.wallet.gateway

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier
import automate.profit.autocoin.spi.exchange.currency.CurrencyBalance


interface WalletServiceGateway<T> {

    fun getCurrencyBalance(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
    ): CurrencyBalance

    fun getCurrencyBalances(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): List<CurrencyBalance>

}

