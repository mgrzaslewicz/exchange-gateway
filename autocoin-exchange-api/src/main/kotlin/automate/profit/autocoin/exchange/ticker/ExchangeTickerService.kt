package automate.profit.autocoin.exchange.ticker

import automate.profit.autocoin.exchange.currency.CurrencyPair

class InvalidCurrencyPairException(currencyPair: CurrencyPair) : Exception(currencyPair.toString())

interface UserExchangeTickerService {
    @Throws(InvalidCurrencyPairException::class)
    fun getTicker(currencyPair: CurrencyPair): Ticker
    fun getTickers(currencyPairs: Collection<CurrencyPair>): List<Ticker>
}

interface ExchangeTickerService {
    fun getTicker(exchangeName: String, currencyPair: CurrencyPair): Ticker
    fun getTickers(exchangeName: String, currencyPairs: Collection<CurrencyPair>): List<Ticker>
}
