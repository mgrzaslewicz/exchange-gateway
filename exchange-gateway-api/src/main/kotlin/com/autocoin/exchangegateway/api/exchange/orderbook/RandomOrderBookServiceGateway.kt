package com.autocoin.exchangegateway.api.exchange.orderbook

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide
import com.autocoin.exchangegateway.spi.exchange.orderbook.gateway.OrderBookServiceGateway
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Clock
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderBook as SpiOrderBook

/**
 * Random order books for testing purposes
 */
class RandomOrderBookServiceGateway(private val clock: Clock) : OrderBookServiceGateway {
    override fun getOrderBook(
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
    ): SpiOrderBook {
        return OrderBook(
            exchangeName = exchangeName,
            currencyPair = currencyPair,
            buyOrders =
            (1..100).map {
                OrderInOrderBook(
                    exchangeName = exchangeName,
                    side = OrderSide.BID_BUY,
                    price = BigDecimal(Math.random()).abs().setScale(8, RoundingMode.HALF_EVEN),
                    currencyPair = currencyPair,
                    orderedAmount = BigDecimal(Math.random()).abs().setScale(8, RoundingMode.HALF_EVEN),
                    receivedAtMillis = clock.millis(),
                    exchangeTimestampMillis = null,
                )
            },
            sellOrders = (1..100).map {
                OrderInOrderBook(
                    exchangeName = exchangeName,
                    side = OrderSide.ASK_SELL,
                    price = BigDecimal(Math.random()).abs().setScale(8, RoundingMode.HALF_EVEN),
                    currencyPair = currencyPair,
                    orderedAmount = BigDecimal(Math.random()).abs().setScale(8, RoundingMode.HALF_EVEN),
                    receivedAtMillis = clock.millis(),
                    exchangeTimestampMillis = null,
                )
            },
            receivedAtMillis = clock.millis(),
            exchangeTimestampMillis = null,
        )
    }
}
