package com.autocoin.exchangegateway.spi.exchange.wallet.gateway

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyBalance
import com.autocoin.exchangegateway.spi.exchange.wallet.WithdrawResult
import com.autocoin.exchangegateway.spi.exchange.wallet.service.WalletService
import java.math.BigDecimal

class DelegateWalletServiceGateway<T>(
    private val walletServices: Map<ExchangeName, WalletService<T>>,
) : WalletServiceGateway<T> {
    override fun getCurrencyBalance(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
    ): CurrencyBalance {
        return walletServices.getValue(exchangeName).getCurrencyBalance(apiKey, currencyCode)
    }

    override fun getCurrencyBalances(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): List<CurrencyBalance> {
        return walletServices.getValue(exchangeName).getCurrencyBalances(apiKey)
    }

    override fun withdraw(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
        amount: BigDecimal,
        address: String
    ): WithdrawResult {
        return walletServices.getValue(exchangeName).withdraw(apiKey, currencyCode, amount, address)
    }

}
