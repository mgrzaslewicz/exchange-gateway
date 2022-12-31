package automate.profit.autocoin.spi.exchange.wallet.service

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.currency.CurrencyBalance
import java.util.function.Supplier

interface WalletService {
    val exchangeName: ExchangeName

    fun getCurrencyBalance(
        apiKey: Supplier<ApiKey>,
        currencyCode: String,
    ): CurrencyBalance

    fun getCurrencyBalances(apiKey: Supplier<ApiKey>): List<CurrencyBalance>

}

