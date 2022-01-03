package automate.profit.autocoin.exchange.ticker

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.peruser.UserExchangeServicesFactory
import java.util.concurrent.ConcurrentHashMap

class XchangeExchangeTickerService(private val userExchangeServicesFactory: UserExchangeServicesFactory) : ExchangeTickerService {
    override fun getTicker(exchangeName: String, currencyPair: CurrencyPair): Ticker {
        val tickerService = userExchangeServicesFactory.createTickerService(exchangeName)
        return tickerService.getTicker(currencyPair)
    }

    override fun getTickers(exchangeName: String, currencyPairs: Collection<CurrencyPair>): List<Ticker> {
        val tickerService = userExchangeServicesFactory.createTickerService(exchangeName)
        return tickerService.getTickers(currencyPairs)
    }
}

class CachingXchangeExchangeTickerService(private val userExchangeServicesFactory: UserExchangeServicesFactory) : ExchangeTickerService {
    private val cache = ConcurrentHashMap<String, UserExchangeTickerService>()

    override fun getTicker(exchangeName: String, currencyPair: CurrencyPair): Ticker {
        return cache.computeIfAbsent(exchangeName.lowercase()) {
            userExchangeServicesFactory.createTickerService(exchangeName)
        }.getTicker(currencyPair)
    }

    override fun getTickers(exchangeName: String, currencyPairs: Collection<CurrencyPair>): List<Ticker> {
        return cache.computeIfAbsent(exchangeName.lowercase()) {
            userExchangeServicesFactory.createTickerService(exchangeName)
        }.getTickers(currencyPairs)
    }
}