package automate.profit.autocoin.exchange.orderbook

import java.math.BigDecimal
import java.math.RoundingMode.HALF_EVEN

data class OrderBook(
        val buyOrders: List<OrderBookExchangeOrder>,
        val sellOrders: List<OrderBookExchangeOrder>
) {

    /**
     * @return null when order cannot be filled for given amount, weighted average price otherwise
     */
    fun getWeightedAverageSellPrice(baseCurrencyAmount: BigDecimal): BigDecimal? {
        return getWeightedAveragePrice(sellOrders, baseCurrencyAmount)
    }

    private fun getWeightedAveragePrice(orders: List<OrderBookExchangeOrder>, baseCurrencyAmount: BigDecimal): BigDecimal? {
        var baseCurrencyToSellAmountLeft = baseCurrencyAmount
        var sum = BigDecimal.ZERO
        run loop@{
            orders.forEach {
                val amountFilledAtThisOrder = baseCurrencyToSellAmountLeft.min(it.orderedAmount)
                baseCurrencyToSellAmountLeft = baseCurrencyToSellAmountLeft.minus(amountFilledAtThisOrder)
                sum += amountFilledAtThisOrder.multiply(it.price)
                if (baseCurrencyToSellAmountLeft <= BigDecimal.ZERO) {
                    return@loop
                }
            }
        }
        return if (baseCurrencyToSellAmountLeft <= BigDecimal.ZERO) {
            sum.setScale(8, HALF_EVEN).divide(baseCurrencyAmount, HALF_EVEN).setScale(8, HALF_EVEN)
        } else {
            null
        }
    }

    /**
     * @return null when order cannot be filled for given amount, weighted average price otherwise
     */
    fun getWeightedAverageBuyPrice(baseCurrencyAmount: BigDecimal): BigDecimal? {
        return getWeightedAveragePrice(buyOrders, baseCurrencyAmount)
    }

    /**
     * Scenario: order book has orders for A/B currency pair and you have some amount of eg USD.
     * What's the average B/USD price going to be?
     * @param otherCurrencyPrice price of counterCurrency/otherCurrency
     * @return null when order cannot be filled for given otherCurrencyAmount
     */
    fun getWeightedAverageBuyPriceInOtherCurrency(otherCurrencyAmount: BigDecimal, otherCurrencyPrice: BigDecimal): BigDecimal? {
        return getWeightedAveragePriceInOtherCurrency(buyOrders, otherCurrencyAmount, otherCurrencyPrice)
    }

    fun getWeightedAverageSellPriceInOtherCurrency(otherCurrencyAmount: BigDecimal, otherCurrencyPrice: BigDecimal): BigDecimal? {
        return getWeightedAveragePriceInOtherCurrency(buyOrders, otherCurrencyAmount, otherCurrencyPrice)
    }

    private fun getWeightedAveragePriceInOtherCurrency(orders: List<OrderBookExchangeOrder>, otherCurrencyAmount: BigDecimal, otherCurrencyPrice: BigDecimal): BigDecimal? {
        val counterCurrencyAmountToSpend = otherCurrencyAmount.setScale(8, HALF_EVEN).multiply(otherCurrencyPrice).setScale(8, HALF_EVEN)
        var counterCurrencyAmountToSpendLeft = counterCurrencyAmountToSpend

        var sumAmountBaseCurrrencyBought = BigDecimal.ZERO
        run loop@{
            orders.forEach {
                val counterCurrencyAmountAtOrder = it.orderedAmount.divide(it.price, HALF_EVEN)
                val counterCurrencyFilledAtOrder = counterCurrencyAmountAtOrder.min(counterCurrencyAmountToSpendLeft)
                counterCurrencyAmountToSpendLeft -= counterCurrencyFilledAtOrder
                sumAmountBaseCurrrencyBought += counterCurrencyFilledAtOrder.multiply(it.price)
                if (counterCurrencyAmountToSpendLeft <= BigDecimal.ZERO) {
                    return@loop
                }
            }
        }
        return if (counterCurrencyAmountToSpendLeft <= BigDecimal.ZERO) {
            val baseCurrencyBoughtValueInCounterCurrency = sumAmountBaseCurrrencyBought.divide(counterCurrencyAmountToSpend, HALF_EVEN)
            return baseCurrencyBoughtValueInCounterCurrency.divide(otherCurrencyPrice, HALF_EVEN).setScale(8, HALF_EVEN)
        } else {
            null
        }
    }

}