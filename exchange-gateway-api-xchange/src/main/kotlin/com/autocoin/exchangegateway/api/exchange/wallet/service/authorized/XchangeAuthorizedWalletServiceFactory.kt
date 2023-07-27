package com.autocoin.exchangegateway.api.exchange.wallet.service.authorized

import com.autocoin.exchangegateway.api.exchange.xchange.XchangeProvider
import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.wallet.service.authorized.AuthorizedWalletService
import com.autocoin.exchangegateway.spi.exchange.wallet.service.authorized.AuthorizedWalletServiceFactory

class XchangeAuthorizedWalletServiceFactory<T>(
    private val xchangeProvider: XchangeProvider<T>,
    private val expectedTradingWalletNameWhenMultipleExist: String = "trade",
) : AuthorizedWalletServiceFactory<T> {
    override fun createAuthorizedWalletService(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedWalletService<T> {
        val xchange = xchangeProvider(
            exchange = exchange,
            apiKey = apiKey,
        )
        return XchangeAuthorizedWalletService(
            exchange = exchange,
            apiKey = apiKey,
            delegate = xchange.accountService,
            expectedTradingWalletNameWhenMultipleExist = expectedTradingWalletNameWhenMultipleExist,
        )
    }

}
