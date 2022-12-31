package automate.profit.autocoin.spi.exchange.wallet.service

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier

interface WalletServiceFactory<T> {

    fun createWalletService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): WalletService<T>

}
