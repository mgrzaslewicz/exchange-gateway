package com.autocoin.exchangegateway.spi.exchange.wallet.service

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface WalletServiceFactory<T> {

    fun createWalletService(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): WalletService<T>

}
