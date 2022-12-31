package automate.profit.autocoin.spi.exchange.wallet.service

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey

interface WalletServiceFactory {

    fun createWalletService(
        exchangeName: ExchangeName,
        apiKey: ApiKey,
    ): WalletService

}
