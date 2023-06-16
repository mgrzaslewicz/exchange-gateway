package com.autocoin.exchangegateway.spi.exchange.wallet.service.authorized

import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyBalance
import com.autocoin.exchangegateway.spi.exchange.wallet.WithdrawResult
import java.math.BigDecimal

interface AuthorizedWalletService<T> : com.autocoin.exchangegateway.spi.exchange.AuthorizedService<T> {

    fun getCurrencyBalance(currencyCode: String): CurrencyBalance

    fun getCurrencyBalances(): List<CurrencyBalance>

    fun withdraw(
        currencyCode: String,
        amount: BigDecimal,
        address: String,
    ): WithdrawResult

}
