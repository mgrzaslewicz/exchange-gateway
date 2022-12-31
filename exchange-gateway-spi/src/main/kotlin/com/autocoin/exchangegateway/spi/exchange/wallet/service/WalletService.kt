package com.autocoin.exchangegateway.spi.exchange.wallet.service

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyBalance

interface WalletService<T> {
    val exchangeName: ExchangeName

    fun getCurrencyBalance(
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
    ): CurrencyBalance

    fun getCurrencyBalances(apiKey: ApiKeySupplier<T>): List<CurrencyBalance>

}

