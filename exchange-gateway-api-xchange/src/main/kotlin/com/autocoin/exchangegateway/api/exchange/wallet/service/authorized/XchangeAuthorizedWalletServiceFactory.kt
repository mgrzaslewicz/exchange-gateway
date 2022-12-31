package com.autocoin.exchangegateway.api.exchange.wallet.service.authorized

import com.autocoin.exchangegateway.api.exchange.xchange.XchangeProvider
import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.wallet.service.authorized.AuthorizedWalletService
import com.autocoin.exchangegateway.spi.exchange.wallet.service.authorized.AuthorizedWalletServiceFactory

class XchangeAuthorizedWalletServiceFactory<T>(
    private val xchangeProvider: XchangeProvider<T>,
    private val expectedTradingWalletNameWhenMultipleExist: String = "trade",
) : AuthorizedWalletServiceFactory<T> {
    override fun createAuthorizedWalletService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedWalletService<T> {
        val xchange = xchangeProvider(exchangeName = exchangeName, apiKey = apiKey)
        return XchangeAuthorizedWalletService(
            exchangeName = exchangeName,
            apiKey = apiKey,
            delegate = xchange.accountService,
            expectedTradingWalletNameWhenMultipleExist = expectedTradingWalletNameWhenMultipleExist,
        )
    }

}
