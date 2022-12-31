package automate.profit.autocoin.exchange.ticker

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.peruser.UserExchangeServicesFactory
import automate.profit.autocoin.exchange.ratelimiter.RateLimiterBehavior
import java.util.concurrent.ConcurrentHashMap

class XchangeExchangeTickerService(private val userExchangeServicesFactory: UserExchangeServicesFactory) : ExchangeTickerService {
    override fun getTicker(exchangeName: String, currencyPair: CurrencyPair, rateLimiterBehaviour: RateLimiterBehavior): Ticker {
        val tickerService = userExchangeServicesFactory.createTickerService(exchangeName)
        return tickerService.getTicker(currencyPair, rateLimiterBehaviour)
    }

    override fun getTickers(exchangeName: String, currencyPairs: Collection<CurrencyPair>, rateLimiterBehaviour: RateLimiterBehavior): List<Ticker> {
        val tickerService = userExchangeServicesFactory.createTickerService(exchangeName)
        return tickerService.getTickers(currencyPairs, rateLimiterBehaviour)
    }
}

class CachingXchangeExchangeTickerService(private val userExchangeServicesFactory: UserExchangeServicesFactory) : ExchangeTickerService {
    private val cache = ConcurrentHashMap<String, UserExchangeTickerService>()

    override fun getTicker(exchangeName: String, currencyPair: CurrencyPair, rateLimiterBehaviour: RateLimiterBehavior): Ticker {
        return cache.computeIfAbsent(exchangeName.lowercase()) {
            userExchangeServicesFactory.createTickerService(exchangeName)
        }.getTicker(currencyPair, rateLimiterBehaviour)
    }

    override fun getTickers(exchangeName: String, currencyPairs: Collection<CurrencyPair>, rateLimiterBehaviour: RateLimiterBehavior): List<Ticker> {
        return cache.computeIfAbsent(exchangeName.lowercase()) {
            userExchangeServicesFactory.createTickerService(exchangeName)
        }.getTickers(currencyPairs, rateLimiterBehaviour)
    }
}
