package com.autocoin.exchangegateway.spi.exchange.wallet.service

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyBalance
import com.autocoin.exchangegateway.spi.exchange.wallet.WithdrawResult
import java.math.BigDecimal

interface WalletService<T> {
    val exchange: Exchange

    fun getCurrencyBalance(
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
    ): CurrencyBalance

    fun getCurrencyBalances(apiKey: ApiKeySupplier<T>): List<CurrencyBalance>

    fun withdraw(
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
        amount: BigDecimal,
        address: String,
    ): WithdrawResult

}

