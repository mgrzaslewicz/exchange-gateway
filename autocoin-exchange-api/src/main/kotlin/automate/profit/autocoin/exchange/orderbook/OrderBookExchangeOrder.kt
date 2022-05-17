package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.currency.ExchangeCache
import automate.profit.autocoin.exchange.order.ExchangeOrderType
import java.math.BigDecimal

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
    val receivedAtMillis: Long,
    val exchangeTimestampMillis: Long?,
) {

    companion object {
        operator fun invoke(
            exchangeName: String,
            type: ExchangeOrderType,
            orderedAmount: BigDecimal,
            price: BigDecimal,
            currencyPair: CurrencyPair,
            receivedAtMillis: Long,
            exchangeTimestampMillis: Long?,
        ): OrderBookExchangeOrder {
            return OrderBookExchangeOrder(
                exchangeName = ExchangeCache.get(exchangeName),
                type = type,
                orderedAmount = orderedAmount,
                price = price,
                currencyPair = currencyPair,
                receivedAtMillis = receivedAtMillis,
                exchangeTimestampMillis = exchangeTimestampMillis,
            )
        }
    }

    fun valueInCounterCurrency() = orderedAmount.multiply(price)
}
