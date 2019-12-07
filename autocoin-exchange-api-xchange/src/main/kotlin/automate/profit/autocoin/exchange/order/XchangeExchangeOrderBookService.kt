package automate.profit.autocoin.exchange.order

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.currency.toXchangeCurrencyPair
import automate.profit.autocoin.exchange.orderbook.OrderBook
import automate.profit.autocoin.exchange.peruser.UserExchangeServicesFactory
import automate.profit.autocoin.exchange.peruser.toOrderBookExchangeOrder
import org.knowm.xchange.service.marketdata.MarketDataService

class XchangeExchangeOrderBookService(private val userExchangeServicesFactory: UserExchangeServicesFactory) : ExchangeOrderBookService {
    override fun getOrderBook(exchangeName: String, currencyPair: CurrencyPair): OrderBook {
        val orderBookService = userExchangeServicesFactory.createOrderBookService(exchangeName)
        return orderBookService.getOrderBook(currencyPair)
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