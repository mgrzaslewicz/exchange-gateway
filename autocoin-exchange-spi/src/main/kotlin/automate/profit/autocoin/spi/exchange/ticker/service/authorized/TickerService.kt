package automate.profit.autocoin.spi.exchange.ticker.service.authorized

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.ticker.Ticker

interface AuthorizedTickerService {
    val exchangeName: ExchangeName
    fun getTicker(currencyPair: CurrencyPair): Ticker
    fun getTickers(currencyPairs: Collection<CurrencyPair>): List<Ticker>
}

