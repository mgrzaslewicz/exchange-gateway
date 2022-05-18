package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.currency.toXchangeCurrencyPair
import automate.profit.autocoin.exchange.peruser.UserExchangeServicesFactory
import automate.profit.autocoin.exchange.peruser.toOrderBookExchangeOrder
import automate.profit.autocoin.exchange.ratelimiter.ExchangeRateLimiter
import automate.profit.autocoin.exchange.ratelimiter.RateLimiterBehavior
import automate.profit.autocoin.exchange.ratelimiter.acquireWith
import automate.profit.autocoin.exchange.time.TimeMillisProvider
import org.knowm.xchange.service.marketdata.MarketDataService
import java.util.concurrent.ConcurrentHashMap

class XchangeExchangeOrderBookService(private val userExchangeServicesFactory: UserExchangeServicesFactory) : ExchangeOrderBookService {
    override fun getOrderBook(exchangeName: String, currencyPair: CurrencyPair, rateLimiterBehaviour: RateLimiterBehavior): OrderBook {
        val orderBookService = userExchangeServicesFactory.createOrderBookService(exchangeName)
        return orderBookService.getOrderBook(currencyPair, rateLimiterBehaviour)
    }
}

class CachingXchangeExchangeOrderBookService(private val userExchangeServicesFactory: UserExchangeServicesFactory) : ExchangeOrderBookService {
    private val cache = ConcurrentHashMap<String, UserExchangeOrderBookService>()

    override fun getOrderBook(exchangeName: String, currencyPair: CurrencyPair, rateLimiterBehaviour: RateLimiterBehavior): OrderBook {
        return cache.computeIfAbsent(exchangeName.lowercase()) {
            userExchangeServicesFactory.createOrderBookService(exchangeName)
        }.getOrderBook(currencyPair, rateLimiterBehaviour)
    }
}

class XchangeUserExchangeOrderBookService(
    private val marketDataService: MarketDataService,
    private val exchangeName: String,
    private val exchangeRateLimiter: ExchangeRateLimiter,
    private val timeMillisProvider: TimeMillisProvider,
) : UserExchangeOrderBookService {

    override fun getOrderBook(currencyPair: CurrencyPair, rateLimiterBehaviour: RateLimiterBehavior): OrderBook {
        exchangeRateLimiter.acquireWith(rateLimiterBehaviour) { "[$exchangeName] Could not acquire permit to getOrderBook" }
        return marketDataService.getOrderBook(currencyPair.toXchangeCurrencyPair()).toOrderBook(
            exchangeName = exchangeName,
            currentTimeMillis = timeMillisProvider.now()
        )
    }

}

fun org.knowm.xchange.dto.marketdata.OrderBook.toOrderBook(exchangeName: String, currentTimeMillis: Long) = OrderBook(
    buyOrders = bids.map { it.toOrderBookExchangeOrder(exchangeName = exchangeName, receivedAtMillis = currentTimeMillis) },
    sellOrders = asks.map { it.toOrderBookExchangeOrder(exchangeName = exchangeName, receivedAtMillis = currentTimeMillis) },
    receivedAtMillis = currentTimeMillis,
    exchangeTimestampMillis = timeStamp?.time,
)
