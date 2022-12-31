package com.autocoin.exchangegateway.spi.exchange.wallet.service

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface WalletServiceFactory<T> {

    fun createWalletService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): WalletService<T>

}
