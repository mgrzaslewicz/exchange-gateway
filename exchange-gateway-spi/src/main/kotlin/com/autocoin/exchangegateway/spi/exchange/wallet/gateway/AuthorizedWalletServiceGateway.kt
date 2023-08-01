package com.autocoin.exchangegateway.spi.exchange.wallet.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyBalance
import com.autocoin.exchangegateway.spi.exchange.wallet.WithdrawResult
import java.math.BigDecimal


interface AuthorizedWalletServiceGateway {

    fun getCurrencyBalance(
        exchange: Exchange,
        currencyCode: String,
    ): CurrencyBalance

    fun getCurrencyBalances(
        exchange: Exchange,
    ): List<CurrencyBalance>

    fun withdraw(
        exchange: Exchange,
        currencyCode: String,
        amount: BigDecimal,
        address: String,
    ): WithdrawResult

}

