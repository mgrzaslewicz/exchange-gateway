package automate.profit.autocoin.spi.exchange.wallet.gateway

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier
import automate.profit.autocoin.spi.exchange.currency.CurrencyBalance
import automate.profit.autocoin.spi.exchange.wallet.service.WalletService

class DelegateWalletServiceGateway<T>(
    private val walletServices: Map<ExchangeName, WalletService<T>>,
) : WalletServiceGateway<T> {
    override fun getCurrencyBalance(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
    ): CurrencyBalance {
        return walletServices.getValue(exchangeName).getCurrencyBalance(apiKey, currencyCode)
    }

    override fun getCurrencyBalances(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): List<CurrencyBalance> {
        return walletServices.getValue(exchangeName).getCurrencyBalances(apiKey)
    }

}
