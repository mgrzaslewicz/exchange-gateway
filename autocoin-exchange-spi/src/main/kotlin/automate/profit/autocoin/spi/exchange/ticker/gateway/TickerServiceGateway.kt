package automate.profit.autocoin.spi.exchange.ticker.gateway

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.ticker.Ticker
import java.util.function.Supplier

class InvalidCurrencyPairException(currencyPair: CurrencyPair) : Exception(currencyPair.toString())

interface TickerServiceGateway {
    fun getTicker(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>?, currencyPair: CurrencyPair): Ticker
    fun getTickers(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>?, currencyPairs: Collection<CurrencyPair>): List<Ticker>
}


