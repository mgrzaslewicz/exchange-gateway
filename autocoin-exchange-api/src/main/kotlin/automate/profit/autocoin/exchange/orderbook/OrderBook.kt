package automate.profit.autocoin.exchange.orderbook

import java.math.BigDecimal
import java.math.RoundingMode.HALF_EVEN

data class OrderBook(
        val buyOrders: List<OrderBookExchangeOrder>,
        val sellOrders: List<OrderBookExchangeOrder>
) {

    fun buyOrderBookValueInCounterCurrency(): BigDecimal = buyOrders.fold(BigDecimal.ZERO) { acc, order -> acc + order.valueInCounterCurrency() }

    /**
     * @return null when order cannot be filled for given amount, weighted average price otherwise
     */
    fun getWeightedAverageBuyPrice(baseCurrencyAmount: BigDecimal): BigDecimal? {
        var baseCurrencyToSellAmountLeft = baseCurrencyAmount
        var sum = BigDecimal.ZERO
        run loop@{
            buyOrders.forEach {
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
     * Scenario: order book has orders for XRP/BTC currency pair and you have some amount of eg USD = n.
     * What's the average XRP/BTC price going to be when you use BTC currency in amount with value equal to n USD?
     * @param otherCurrencyPrice price of counterCurrency/otherCurrency
     * @return null when order cannot be filled for given otherCurrencyAmount
     */
    fun getWeightedAverageBuyPrice(otherCurrencyAmount: BigDecimal, otherCurrencyPrice: BigDecimal): BigDecimal? {
        val counterCurrencyAmountToSpend = otherCurrencyAmount.setScale(16, HALF_EVEN).divide(otherCurrencyPrice.setScale(16, HALF_EVEN), HALF_EVEN)
        return getWeightedAveragePriceWithCounterCurrencyAmount(counterCurrencyAmountToSpend)
    }

    fun getWeightedAveragePriceWithCounterCurrencyAmount(counterCurrencyAmount: BigDecimal): BigDecimal? {
        var counterCurrencyAmountToSpendLeft = counterCurrencyAmount

        var sumAmountBaseCurrrencyBought = BigDecimal.ZERO
        run loop@{
            buyOrders.forEach {
                val counterCurrencyAmountAtOrder = it.valueInCounterCurrency()
                val counterCurrencyFilledAtOrder = counterCurrencyAmountAtOrder.min(counterCurrencyAmountToSpendLeft)
                counterCurrencyAmountToSpendLeft -= counterCurrencyFilledAtOrder
                val filledRatio = counterCurrencyFilledAtOrder.divide(counterCurrencyAmountAtOrder, HALF_EVEN)
                sumAmountBaseCurrrencyBought += filledRatio.multiply(it.orderedAmount)
                if (counterCurrencyAmountToSpendLeft <= BigDecimal.ZERO) {
                    return@loop
                }
            }
        }
        return if (counterCurrencyAmountToSpendLeft <= BigDecimal.ZERO) {
            return counterCurrencyAmount.divide(sumAmountBaseCurrrencyBought, HALF_EVEN).setScale(8, HALF_EVEN)
        } else {
            null
        }
    }

}