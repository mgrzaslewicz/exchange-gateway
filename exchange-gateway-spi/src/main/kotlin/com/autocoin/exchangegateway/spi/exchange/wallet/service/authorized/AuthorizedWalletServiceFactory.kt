package com.autocoin.exchangegateway.spi.exchange.wallet.service.authorized

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface AuthorizedWalletServiceFactory<T> {

    fun createAuthorizedWalletService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedWalletService<T>

}
