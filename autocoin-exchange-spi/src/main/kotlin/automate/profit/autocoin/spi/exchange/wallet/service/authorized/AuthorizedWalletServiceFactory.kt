package automate.profit.autocoin.spi.exchange.wallet.service.authorized

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier

interface AuthorizedWalletServiceFactory<T> {

    fun createAuthorizedWalletService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedWalletService<T>

}
