package automate.profit.autocoin.spi.exchange.wallet.gateway

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.currency.CurrencyBalance
import java.util.function.Supplier


interface WalletServiceGateway {

    fun getCurrencyBalance(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>, currencyCode: String): CurrencyBalance

    fun getCurrencyBalances(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>): List<CurrencyBalance>

}

