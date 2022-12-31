package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.currency.toXchangeCurrencyPair
import automate.profit.autocoin.exchange.peruser.UserExchangeServicesFactory
import automate.profit.autocoin.exchange.peruser.toOrderBookExchangeOrder
import automate.profit.autocoin.exchange.ratelimiter.ExchangeRateLimiter
import org.knowm.xchange.service.marketdata.MarketDataService
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class XchangeExchangeOrderBookService(private val userExchangeServicesFactory: UserExchangeServicesFactory) : ExchangeOrderBookService {
    override fun getOrderBook(exchangeName: String, currencyPair: CurrencyPair): OrderBook {
        val orderBookService = userExchangeServicesFactory.createOrderBookService(exchangeName)
        return orderBookService.getOrderBook(currencyPair)
    }
}

class CachingXchangeExchangeOrderBookService(private val userExchangeServicesFactory: UserExchangeServicesFactory) : ExchangeOrderBookService {
    private val cache = ConcurrentHashMap<String, UserExchangeOrderBookService>()

    override fun getOrderBook(exchangeName: String, currencyPair: CurrencyPair): OrderBook {
        return cache.computeIfAbsent(exchangeName.lowercase()) {
            userExchangeServicesFactory.createOrderBookService(exchangeName)
        }.getOrderBook(currencyPair)
    }
}

class XchangeUserExchangeOrderBookService(
    private val marketDataService: MarketDataService,
    private val exchangeName: String,
    private val exchangeRateLimiter: ExchangeRateLimiter,
) : UserExchangeOrderBookService {
    override fun getOrderBook(currencyPair: CurrencyPair): OrderBook {
        check(exchangeRateLimiter.tryAcquirePermit(250L, TimeUnit.MILLISECONDS)) { "[$exchangeName] Could not acquire permit for getting order book within 250ms" }
        return marketDataService.getOrderBook(currencyPair.toXchangeCurrencyPair()).toOrderBook(exchangeName)
    }

}

fun org.knowm.xchange.dto.marketdata.OrderBook.toOrderBook(exchangeName: String) = OrderBook(
    buyOrders = bids.map { it.toOrderBookExchangeOrder(exchangeName) },
    sellOrders = asks.map { it.toOrderBookExchangeOrder(exchangeName) }
)
