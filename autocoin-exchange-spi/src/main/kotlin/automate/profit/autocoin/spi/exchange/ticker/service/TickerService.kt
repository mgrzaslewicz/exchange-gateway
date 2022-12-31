package automate.profit.autocoin.spi.exchange.ticker.service

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.ticker.Ticker
import java.util.function.Supplier

interface TickerService {
    val exchangeName: ExchangeName
    fun getTicker(apiKey: Supplier<ApiKey>?, currencyPair: CurrencyPair): Ticker
    fun getTickers(apiKey: Supplier<ApiKey>?, currencyPairs: Collection<CurrencyPair>): List<Ticker>
}

