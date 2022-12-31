package automate.profit.autocoin.exchange.ticker

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.peruser.UserExchangeServicesFactory
import java.util.concurrent.ConcurrentHashMap

class XchangeExchangeTickerService(private val userExchangeServicesFactory: UserExchangeServicesFactory) : ExchangeTickerService {
    override fun getTicker(exchangeName: String, currencyPair: CurrencyPair): Ticker {
        val tickerService = userExchangeServicesFactory.createTickerService(exchangeName)
        return tickerService.getTicker(currencyPair)
    }
}

class CachingXchangeExchangeTickerService(private val userExchangeServicesFactory: UserExchangeServicesFactory) : ExchangeTickerService {
    private val cache = ConcurrentHashMap<String, UserExchangeTickerService>()

    override fun getTicker(exchangeName: String, currencyPair: CurrencyPair): Ticker {
        return cache.computeIfAbsent(exchangeName.toLowerCase()) {
            userExchangeServicesFactory.createTickerService(exchangeName)
        }.getTicker(currencyPair)
    }
}