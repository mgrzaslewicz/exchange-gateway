package com.autocoin.exchangegateway.spi.exchange.wallet.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyBalance
import com.autocoin.exchangegateway.spi.exchange.wallet.WithdrawResult
import com.autocoin.exchangegateway.spi.exchange.wallet.service.authorized.AuthorizedWalletServiceFactory
import java.math.BigDecimal

class AuthorizingWalletServiceGateway<T>(
    private val authorizedWalletServiceFactory: AuthorizedWalletServiceFactory<T>,
) : WalletServiceGateway<T> {
    override fun getCurrencyBalance(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
    ): CurrencyBalance {
        return authorizedWalletServiceFactory
            .createAuthorizedWalletService(exchange = exchange, apiKey = apiKey)
            .getCurrencyBalance(currencyCode = currencyCode)
    }

    override fun getCurrencyBalances(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): List<CurrencyBalance> {
        return authorizedWalletServiceFactory
            .createAuthorizedWalletService(exchange = exchange, apiKey = apiKey)
            .getCurrencyBalances()
    }

    override fun withdraw(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
        amount: BigDecimal,
        address: String
    ): WithdrawResult {
        return authorizedWalletServiceFactory
            .createAuthorizedWalletService(exchange = exchange, apiKey = apiKey)
            .withdraw(currencyCode = currencyCode, amount = amount, address = address)
    }

}
