package com.autocoin.exchangegateway.spi.exchange.wallet.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyBalance
import com.autocoin.exchangegateway.spi.exchange.wallet.WithdrawResult
import java.math.BigDecimal


interface WalletServiceGateway<T> {

    fun getCurrencyBalance(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
    ): CurrencyBalance

    fun getCurrencyBalances(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): List<CurrencyBalance>

    fun withdraw(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
        amount: BigDecimal,
        address: String,
    ): WithdrawResult

}

