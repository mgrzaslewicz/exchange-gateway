package automate.profit.autocoin.spi.exchange.wallet.service.authorized

import automate.profit.autocoin.spi.exchange.AuthorizedService
import automate.profit.autocoin.spi.exchange.currency.CurrencyBalance

interface AuthorizedWalletService<T> : AuthorizedService<T> {

    fun getCurrencyBalance(currencyCode: String): CurrencyBalance

    fun getCurrencyBalances(): List<CurrencyBalance>

}
