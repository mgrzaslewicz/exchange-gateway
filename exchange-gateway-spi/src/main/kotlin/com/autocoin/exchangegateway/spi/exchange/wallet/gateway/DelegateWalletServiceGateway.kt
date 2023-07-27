package com.autocoin.exchangegateway.spi.exchange.wallet.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyBalance
import com.autocoin.exchangegateway.spi.exchange.wallet.WithdrawResult
import com.autocoin.exchangegateway.spi.exchange.wallet.service.WalletService
import java.math.BigDecimal

class DelegateWalletServiceGateway<T>(
    private val walletServices: Map<Exchange, WalletService<T>>,
) : WalletServiceGateway<T> {
    override fun getCurrencyBalance(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
    ): CurrencyBalance {
        return walletServices.getValue(exchange).getCurrencyBalance(apiKey, currencyCode)
    }

    override fun getCurrencyBalances(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): List<CurrencyBalance> {
        return walletServices.getValue(exchange).getCurrencyBalances(apiKey)
    }

    override fun withdraw(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
        amount: BigDecimal,
        address: String
    ): WithdrawResult {
        return walletServices.getValue(exchange).withdraw(apiKey, currencyCode, amount, address)
    }

}
