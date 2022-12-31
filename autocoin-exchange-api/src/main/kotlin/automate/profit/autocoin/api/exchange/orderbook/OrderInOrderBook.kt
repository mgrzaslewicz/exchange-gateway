package automate.profit.autocoin.api.exchange.orderbook

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.currency.ExchangeCache
import automate.profit.autocoin.spi.exchange.order.OrderSide
import java.math.BigDecimal
import automate.profit.autocoin.spi.exchange.orderbook.OrderInOrderBook as SpiOrderInOrderBook

/**
 * Private constructor + invoke operator override in order to apply memory optimization.
 * Note: Private constructor will be called if you call .copy().
 * This will bypass the optimization and thus should be avoided but should not influence code logic in any way.
 * https://stackoverflow.com/a/49561916
 */
data class OrderInOrderBook private constructor(
    override val exchangeName: ExchangeName,
    override val side: OrderSide,
    override val orderedAmount: BigDecimal,
    override val price: BigDecimal,
    override val currencyPair: CurrencyPair,
    override val receivedAtMillis: Long,
    override val exchangeTimestampMillis: Long?,
) : SpiOrderInOrderBook {

    companion object {
        operator fun invoke(
            exchangeName: ExchangeName,
            side: OrderSide,
            orderedAmount: BigDecimal,
            price: BigDecimal,
            currencyPair: CurrencyPair,
            receivedAtMillis: Long,
            exchangeTimestampMillis: Long?,
        ): OrderInOrderBook {
            return OrderInOrderBook(
                exchangeName = ExchangeCache.get(exchangeName),
                side = side,
                orderedAmount = orderedAmount,
                price = price,
                currencyPair = currencyPair,
                receivedAtMillis = receivedAtMillis,
                exchangeTimestampMillis = exchangeTimestampMillis,
            )
        }
    }

}
