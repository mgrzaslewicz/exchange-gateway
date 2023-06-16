package com.autocoin.exchangegateway.spi.exchange.wallet.gateway

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyBalance
import com.autocoin.exchangegateway.spi.exchange.wallet.WithdrawResult
import java.math.BigDecimal


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

    fun withdraw(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
        amount: BigDecimal,
        address: String,
    ): WithdrawResult

}

