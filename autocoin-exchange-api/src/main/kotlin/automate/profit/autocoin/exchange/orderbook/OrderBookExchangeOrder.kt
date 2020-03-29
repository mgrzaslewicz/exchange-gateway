package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.order.ExchangeOrderType
import automate.profit.autocoin.exchange.util.ExchangeCache
import java.math.BigDecimal
import java.time.Instant

/**
 * Private constructor + invoke operator override in order to apply memory optimization.
 * Note: Private constructor will be called if you call .copy().
 * This will bypass the optimization and thus should be avoided but should not influence code logic in any way.
 * https://stackoverflow.com/a/49561916
 */
data class OrderBookExchangeOrder private constructor(
        val exchangeName: String,
        val type: ExchangeOrderType,
        val orderedAmount: BigDecimal,
        val price: BigDecimal,
        val currencyPair: CurrencyPair,
        val timestamp: Instant?
) {

    companion object {
        operator fun invoke(exchangeName: String,
                            type: ExchangeOrderType,
                            orderedAmount: BigDecimal,
                            price: BigDecimal,
                            currencyPair: CurrencyPair,
                            timestamp: Instant?): OrderBookExchangeOrder {
            return OrderBookExchangeOrder(
                    ExchangeCache.get(exchangeName),
                    type,
                    orderedAmount,
                    price,
                    currencyPair,
                    timestamp
            )
        }
    }

    fun valueInCounterCurrency() = orderedAmount.multiply(price)
}