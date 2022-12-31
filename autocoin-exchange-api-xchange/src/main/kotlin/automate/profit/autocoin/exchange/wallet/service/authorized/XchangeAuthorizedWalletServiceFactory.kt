package automate.profit.autocoin.exchange.wallet.service.authorized

import automate.profit.autocoin.exchange.xchange.XchangeProvider
import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.wallet.service.authorized.AuthorizedWalletService
import automate.profit.autocoin.spi.exchange.wallet.service.authorized.AuthorizedWalletServiceFactory
import java.util.function.Supplier

class XchangeAuthorizedWalletServiceFactory(
    private val xchangeProvider: XchangeProvider,
    private val expectedTradingWalletNameWhenMultipleExist: String = "trade",
) : AuthorizedWalletServiceFactory {
    override fun createAuthorizedWalletService(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>): AuthorizedWalletService {
        val xchange = xchangeProvider(exchangeName = exchangeName, apiKey = apiKey)
        return XchangeAuthorizedWalletService(
            exchangeName = exchangeName,
            delegate = xchange.accountService,
            expectedTradingWalletNameWhenMultipleExist = expectedTradingWalletNameWhenMultipleExist,
        )
    }

}
