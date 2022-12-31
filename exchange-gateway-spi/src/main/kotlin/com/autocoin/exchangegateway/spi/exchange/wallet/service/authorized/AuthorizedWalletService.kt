package com.autocoin.exchangegateway.spi.exchange.wallet.service.authorized

import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyBalance

interface AuthorizedWalletService<T> : com.autocoin.exchangegateway.spi.exchange.AuthorizedService<T> {

    fun getCurrencyBalance(currencyCode: String): CurrencyBalance

    fun getCurrencyBalances(): List<CurrencyBalance>

}
