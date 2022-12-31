package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.ratelimiter.RateLimiterBehavior
import automate.profit.autocoin.exchange.ratelimiter.RateLimiterBehavior.WAIT_WITHOUT_TIMEOUT

interface ExchangeOrderBookService {
    fun getOrderBook(exchangeName: String, currencyPair: CurrencyPair, rateLimiterBehaviour: RateLimiterBehavior = WAIT_WITHOUT_TIMEOUT): OrderBook
}

interface UserExchangeOrderBookService {
    fun getOrderBook(currencyPair: CurrencyPair, rateLimiterBehaviour: RateLimiterBehavior = WAIT_WITHOUT_TIMEOUT): OrderBook
}
