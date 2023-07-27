package com.autocoin.exchangegateway.api.exchange.orderbook

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide
import java.math.BigDecimal
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderInOrderBook as SpiOrderInOrderBook

/**
 * Private constructor + invoke operator override in order to apply memory optimization.
 * Note: Private constructor will be called if you call .copy().
 * This will bypass the optimization and thus should be avoided but should not influence code logic in any way.
 * https://stackoverflow.com/a/49561916
 */
data class OrderInOrderBook private constructor(
    override val exchange: Exchange,
    override val side: OrderSide,
    override val orderedAmount: BigDecimal,
    override val price: BigDecimal,
    override val currencyPair: CurrencyPair,
    override val receivedAtMillis: Long,
    override val exchangeTimestampMillis: Long?,
) : SpiOrderInOrderBook {

    companion object {
        operator fun invoke(
            exchange: Exchange,
            side: OrderSide,
            orderedAmount: BigDecimal,
            price: BigDecimal,
            currencyPair: CurrencyPair,
            receivedAtMillis: Long,
            exchangeTimestampMillis: Long?,
        ): OrderInOrderBook {
            return OrderInOrderBook(
                exchange = exchange,
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
