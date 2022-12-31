package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.order.ExchangeOrderType
import automate.profit.autocoin.exchange.ratelimiter.RateLimiterBehavior
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant

/**
 * Random order books for testing purposes
 */
class RandomExchangeOrderBookService : ExchangeOrderBookService {
    override fun getOrderBook(exchangeName: String, currencyPair: CurrencyPair, rateLimiterBehaviour: RateLimiterBehavior): OrderBook {
        return OrderBook(
                buyOrders =
                (1..100).map {
                    OrderBookExchangeOrder(
                            exchangeName = exchangeName,
                            type = ExchangeOrderType.BID_BUY,
                            price = BigDecimal(Math.random()).abs().setScale(8, RoundingMode.HALF_EVEN),
                            currencyPair = currencyPair,
                            orderedAmount = BigDecimal(Math.random()).abs().setScale(8, RoundingMode.HALF_EVEN),
                            timestamp = Instant.now()
                    )
                },
                sellOrders = (1..100).map {
                    OrderBookExchangeOrder(
                            exchangeName = exchangeName,
                            type = ExchangeOrderType.ASK_SELL,
                            price = BigDecimal(Math.random()).abs().setScale(8, RoundingMode.HALF_EVEN),
                            currencyPair = currencyPair,
                            orderedAmount = BigDecimal(Math.random()).abs().setScale(8, RoundingMode.HALF_EVEN),
                            timestamp = Instant.now()
                    )
                }
        )
    }
}
