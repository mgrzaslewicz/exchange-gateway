package automate.profit.autocoin.exchange.ticker

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.ratelimiter.RateLimiterBehavior
import automate.profit.autocoin.exchange.ratelimiter.RateLimiterBehavior.WAIT_WITHOUT_TIMEOUT

class InvalidCurrencyPairException(currencyPair: CurrencyPair) : Exception(currencyPair.toString())

interface UserExchangeTickerService {
    @Throws(InvalidCurrencyPairException::class)
    fun getTicker(currencyPair: CurrencyPair, rateLimiterBehaviour: RateLimiterBehavior = WAIT_WITHOUT_TIMEOUT): Ticker
    fun getTickers(currencyPairs: Collection<CurrencyPair>, rateLimiterBehaviour: RateLimiterBehavior = WAIT_WITHOUT_TIMEOUT): List<Ticker>
}

interface ExchangeTickerService {
    fun getTicker(exchangeName: String, currencyPair: CurrencyPair, rateLimiterBehaviour: RateLimiterBehavior = WAIT_WITHOUT_TIMEOUT): Ticker
    fun getTickers(exchangeName: String, currencyPairs: Collection<CurrencyPair>, rateLimiterBehaviour: RateLimiterBehavior = WAIT_WITHOUT_TIMEOUT): List<Ticker>
}
