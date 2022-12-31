package automate.profit.autocoin.spi.exchange.wallet.gateway

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.currency.CurrencyBalance
import automate.profit.autocoin.spi.exchange.wallet.service.authorized.AuthorizedWalletServiceFactory
import java.util.function.Supplier

class WalletServiceGatewayUsingAuthorizedWalletService(
    private val authorizedWalletServiceFactory: AuthorizedWalletServiceFactory,
) : WalletServiceGateway {
    override fun getCurrencyBalance(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyCode: String,
    ): CurrencyBalance {
        return authorizedWalletServiceFactory
            .createAuthorizedWalletService(exchangeName = exchangeName, apiKey = apiKey)
            .getCurrencyBalance(currencyCode = currencyCode)
    }

    override fun getCurrencyBalances(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
    ): List<CurrencyBalance> {
        return authorizedWalletServiceFactory
            .createAuthorizedWalletService(exchangeName = exchangeName, apiKey = apiKey)
            .getCurrencyBalances()
    }

}
