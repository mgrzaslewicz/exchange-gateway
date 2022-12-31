package automate.profit.autocoin.spi.exchange.wallet.service.authorized

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyBalance

interface AuthorizedWalletService {
    val exchangeName: ExchangeName

    fun getCurrencyBalance(currencyCode: String): CurrencyBalance

    fun getCurrencyBalances(): List<CurrencyBalance>

}
