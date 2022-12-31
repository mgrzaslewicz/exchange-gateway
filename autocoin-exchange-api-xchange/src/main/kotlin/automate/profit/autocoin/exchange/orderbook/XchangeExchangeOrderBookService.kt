package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.currency.toXchangeCurrencyPair
import automate.profit.autocoin.exchange.peruser.UserExchangeServicesFactory
import automate.profit.autocoin.exchange.peruser.toOrderBookExchangeOrder
import org.knowm.xchange.service.marketdata.MarketDataService
import java.util.concurrent.ConcurrentHashMap

class XchangeExchangeOrderBookService(private val userExchangeServicesFactory: UserExchangeServicesFactory) : ExchangeOrderBookService {
    override fun getOrderBook(exchangeName: String, currencyPair: CurrencyPair): OrderBook {
        val orderBookService = userExchangeServicesFactory.createOrderBookService(exchangeName)
        return orderBookService.getOrderBook(currencyPair)
    }
}

class CachingXchangeExchangeOrderBookService(private val userExchangeServicesFactory: UserExchangeServicesFactory) : ExchangeOrderBookService {
    private val cache = ConcurrentHashMap<String, UserExchangeOrderBookService>()

    override fun getOrderBook(exchangeName: String, currencyPair: CurrencyPair): OrderBook {
        return cache.computeIfAbsent(exchangeName.toLowerCase()) {
            userExchangeServicesFactory.createOrderBookService(exchangeName)
        }.getOrderBook(currencyPair)
    }
}

class XchangeUserExchangeOrderBookService(private val marketDataService: MarketDataService, private val exchangeName: String) : UserExchangeOrderBookService {
    override fun getOrderBook(currencyPair: CurrencyPair): OrderBook {
        return marketDataService.getOrderBook(currencyPair.toXchangeCurrencyPair()).toOrderBook(exchangeName)
    }

}

fun org.knowm.xchange.dto.marketdata.OrderBook.toOrderBook(exchangeName: String) = OrderBook(
        buyOrders = bids.map { it.toOrderBookExchangeOrder(exchangeName) },
        sellOrders = asks.map { it.toOrderBookExchangeOrder(exchangeName) }
)