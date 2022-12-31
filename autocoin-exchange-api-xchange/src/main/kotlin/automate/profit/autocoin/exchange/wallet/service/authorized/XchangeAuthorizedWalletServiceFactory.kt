package automate.profit.autocoin.exchange.wallet.service.authorized

import automate.profit.autocoin.exchange.xchange.XchangeProvider
import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier
import automate.profit.autocoin.spi.exchange.wallet.service.authorized.AuthorizedWalletService
import automate.profit.autocoin.spi.exchange.wallet.service.authorized.AuthorizedWalletServiceFactory

class XchangeAuthorizedWalletServiceFactory<T>(
    private val xchangeProvider: XchangeProvider<T>,
    private val expectedTradingWalletNameWhenMultipleExist: String = "trade",
) : AuthorizedWalletServiceFactory<T> {
    override fun createAuthorizedWalletService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedWalletService<T> {
        val xchange = xchangeProvider(exchangeName = exchangeName, apiKey = apiKey)
        return XchangeAuthorizedWalletService(
            exchangeName = exchangeName,
            apiKey = apiKey,
            delegate = xchange.accountService,
            expectedTradingWalletNameWhenMultipleExist = expectedTradingWalletNameWhenMultipleExist,
        )
    }

}
