package automate.profit.autocoin.spi.exchange.wallet.gateway

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier
import automate.profit.autocoin.spi.exchange.currency.CurrencyBalance
import automate.profit.autocoin.spi.exchange.wallet.service.authorized.AuthorizedWalletServiceFactory

class WalletServiceGatewayUsingAuthorizedWalletService<T>(
    private val authorizedWalletServiceFactory: AuthorizedWalletServiceFactory<T>,
) : WalletServiceGateway<T> {
    override fun getCurrencyBalance(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
    ): CurrencyBalance {
        return authorizedWalletServiceFactory
            .createAuthorizedWalletService(exchangeName = exchangeName, apiKey = apiKey)
            .getCurrencyBalance(currencyCode = currencyCode)
    }

    override fun getCurrencyBalances(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): List<CurrencyBalance> {
        return authorizedWalletServiceFactory
            .createAuthorizedWalletService(exchangeName = exchangeName, apiKey = apiKey)
            .getCurrencyBalances()
    }

}
