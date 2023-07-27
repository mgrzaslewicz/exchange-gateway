package com.autocoin.exchangegateway.spi.exchange.orderbook

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import java.math.BigDecimal


interface OrderBook {
    val exchange: Exchange
    val currencyPair: CurrencyPair
    val buyOrders: List<OrderInOrderBook>
    val sellOrders: List<OrderInOrderBook>
    val receivedAtMillis: Long
    val exchangeTimestampMillis: Long?

    fun deepEquals(other: OrderBook): Boolean {
        return if (buyOrders.size == other.buyOrders.size && sellOrders.size == other.sellOrders.size) {
            buyOrders.forEachIndexed { index, it ->
                if (it != other.buyOrders[index]) {
                    return false
                }
            }
            sellOrders.forEachIndexed { index, it ->
                if (it != other.sellOrders[index]) {
                    return false
                }
            }
            true
        }
        else {
            false
        }
    }

    fun buyOrderBookValueInCounterCurrency(): BigDecimal = buyOrders.fold(BigDecimal.ZERO) { acc, order -> acc + order.valueInCounterCurrency() }

    /**
     * @return null when order cannot be filled for given amount, weighted average price otherwise
     */
    fun getWeightedAverageBuyPrice(baseCurrencyAmount: BigDecimal): OrderBookAveragePrice?

    fun getWeightedAverageSellPrice(baseCurrencyAmount: BigDecimal): OrderBookAveragePrice?

    /**
     * Scenario: order book has orders for XRP/BTC currency pair and you have some amount of eg USD = n.
     * What's the average XRP/BTC price going to be when you use BTC currency in amount with value equal to n USD?
     * @param otherCurrencyPrice price of counterCurrency/otherCurrency
     * @return null when order cannot be filled for given otherCurrencyAmount
     */
    fun getWeightedAverageBuyPrice(
        otherCurrencyAmount: BigDecimal,
        otherCurrencyPrice: BigDecimal,
    ): OrderBookAveragePrice?

    fun getWeightedAverageSellPrice(
        otherCurrencyAmount: BigDecimal,
        otherCurrencyPrice: BigDecimal,
    ): OrderBookAveragePrice?

}
