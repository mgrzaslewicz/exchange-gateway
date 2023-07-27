package com.autocoin.exchangegateway.spi.exchange.wallet.service.authorized

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface AuthorizedWalletServiceFactory<T> {

    fun createAuthorizedWalletService(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedWalletService<T>

}
