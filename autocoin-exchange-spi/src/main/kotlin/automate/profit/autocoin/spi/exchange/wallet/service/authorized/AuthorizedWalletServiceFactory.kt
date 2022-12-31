package automate.profit.autocoin.spi.exchange.wallet.service.authorized

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import java.util.function.Supplier

interface AuthorizedWalletServiceFactory {

    fun createAuthorizedWalletService(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
    ): AuthorizedWalletService

}
