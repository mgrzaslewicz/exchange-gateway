package automate.profit.autocoin.exchange.ticker

import automate.profit.autocoin.exchange.currency.CurrencyPair

interface ExchangeTickerService {
    fun getTicker(exchangeName: String, currencyPair: CurrencyPair): Ticker
}
