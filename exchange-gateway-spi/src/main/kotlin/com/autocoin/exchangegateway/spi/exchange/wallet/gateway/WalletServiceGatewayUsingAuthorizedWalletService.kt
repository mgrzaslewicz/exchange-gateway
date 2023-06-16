package com.autocoin.exchangegateway.spi.exchange.wallet.gateway

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyBalance
import com.autocoin.exchangegateway.spi.exchange.wallet.WithdrawResult
import com.autocoin.exchangegateway.spi.exchange.wallet.service.authorized.AuthorizedWalletServiceFactory
import java.math.BigDecimal

class WalletServiceGatewayUsingAuthorizedWalletService<T>(
    private val authorizedWalletServiceFactory: AuthorizedWalletServiceFactory<T>,
) : WalletServiceGateway<T> {
    override fun getCurrencyBalance(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
    ): CurrencyBalance {
        return authorizedWalletServiceFactory
            .createAuthorizedWalletService(exchangeName = exchangeName, apiKey = apiKey)
            .getCurrencyBalance(currencyCode = currencyCode)
    }

    override fun getCurrencyBalances(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): List<CurrencyBalance> {
        return authorizedWalletServiceFactory
            .createAuthorizedWalletService(exchangeName = exchangeName, apiKey = apiKey)
            .getCurrencyBalances()
    }

    override fun withdraw(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
        amount: BigDecimal,
        address: String
    ): WithdrawResult {
        return authorizedWalletServiceFactory
            .createAuthorizedWalletService(exchangeName = exchangeName, apiKey = apiKey)
            .withdraw(currencyCode = currencyCode, amount = amount, address = address)
    }

}
