package com.autocoin.exchangegateway.spi.exchange.wallet.gateway

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyBalance


interface WalletServiceGateway<T> {

    fun getCurrencyBalance(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
    ): CurrencyBalance

    fun getCurrencyBalances(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): List<CurrencyBalance>

}

