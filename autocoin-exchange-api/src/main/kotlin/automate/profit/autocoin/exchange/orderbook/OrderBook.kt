package automate.profit.autocoin.exchange.orderbook

import java.math.BigDecimal
import java.math.RoundingMode.HALF_EVEN

data class OrderBookAveragePrice(
        /**
         * scale = 8
         */
        val averagePrice: BigDecimal,
        /**
         * scale = 8
         */
        val baseCurrencyAmount: BigDecimal
)

data class OrderBook(
        val buyOrders: List<OrderBookExchangeOrder>,
        val sellOrders: List<OrderBookExchangeOrder>
) {

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
        } else {
            false
        }
    }

    fun buyOrderBookValueInCounterCurrency(): BigDecimal = buyOrders.fold(BigDecimal.ZERO) { acc, order -> acc + order.valueInCounterCurrency() }

    /**
     * @return null when order cannot be filled for given amount, weighted average price otherwise
     */
    fun getWeightedAverageBuyPrice(baseCurrencyAmount: BigDecimal): OrderBookAveragePrice? {
        return getWeightedAveragePrice(baseCurrencyAmount, buyOrders)
    }

    fun getWeightedAverageSellPrice(baseCurrencyAmount: BigDecimal): OrderBookAveragePrice? {
        return getWeightedAveragePrice(baseCurrencyAmount, sellOrders)
    }

    private fun getWeightedAveragePrice(baseCurrencyAmount: BigDecimal, orders: List<OrderBookExchangeOrder>): OrderBookAveragePrice? {
        var baseCurrencyToUseAmountLeft = baseCurrencyAmount
        var sum = BigDecimal.ZERO
        run loop@{
            orders.forEach {
                val amountFilledAtThisOrder = baseCurrencyToUseAmountLeft.min(it.orderedAmount)
                baseCurrencyToUseAmountLeft = baseCurrencyToUseAmountLeft.minus(amountFilledAtThisOrder)
                sum += amountFilledAtThisOrder.multiply(it.price)
                if (baseCurrencyToUseAmountLeft <= BigDecimal.ZERO) {
                    return@loop
                }
            }
        }
        return if (baseCurrencyToUseAmountLeft <= BigDecimal.ZERO) {
            return OrderBookAveragePrice(
                    averagePrice = sum.setScale(8, HALF_EVEN).divide(baseCurrencyAmount, HALF_EVEN).setScale(8, HALF_EVEN),
                    baseCurrencyAmount = baseCurrencyAmount.setScale(8, HALF_EVEN)
            )
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
    fun getWeightedAverageBuyPrice(otherCurrencyAmount: BigDecimal, otherCurrencyPrice: BigDecimal): OrderBookAveragePrice? {
        val counterCurrencyAmountToSpend = otherCurrencyAmount.setScale(16, HALF_EVEN).divide(otherCurrencyPrice.setScale(16, HALF_EVEN), HALF_EVEN)
        return getWeightedAveragePriceWithCounterCurrencyAmount(counterCurrencyAmountToSpend, buyOrders)
    }

    fun getWeightedAverageSellPrice(otherCurrencyAmount: BigDecimal, otherCurrencyPrice: BigDecimal): OrderBookAveragePrice? {
        val counterCurrencyAmountToSpend = otherCurrencyAmount.setScale(16, HALF_EVEN).divide(otherCurrencyPrice.setScale(16, HALF_EVEN), HALF_EVEN)
        return getWeightedAveragePriceWithCounterCurrencyAmount(counterCurrencyAmountToSpend, sellOrders)
    }

    private fun getWeightedAverageBuyPrice(otherCurrencyAmount: BigDecimal, otherCurrencyPrice: BigDecimal, orders: List<OrderBookExchangeOrder>): OrderBookAveragePrice? {
        val counterCurrencyAmountToSpend = otherCurrencyAmount.setScale(16, HALF_EVEN).divide(otherCurrencyPrice.setScale(16, HALF_EVEN), HALF_EVEN)
        return getWeightedAveragePriceWithCounterCurrencyAmount(counterCurrencyAmountToSpend, orders)
    }

    private fun getWeightedAveragePriceWithCounterCurrencyAmount(counterCurrencyAmount: BigDecimal, orders: List<OrderBookExchangeOrder>): OrderBookAveragePrice? {
        var counterCurrencyAmountToUseLeft = counterCurrencyAmount

        var sumAmountBaseCurrrencyUsed = BigDecimal.ZERO
        run loop@{
            orders.forEach {
                val counterCurrencyAmountAtOrder = it.valueInCounterCurrency()
                val counterCurrencyFilledAtOrder = counterCurrencyAmountAtOrder.min(counterCurrencyAmountToUseLeft)
                counterCurrencyAmountToUseLeft -= counterCurrencyFilledAtOrder
                val filledRatio = counterCurrencyFilledAtOrder.divide(counterCurrencyAmountAtOrder, HALF_EVEN)
                sumAmountBaseCurrrencyUsed += filledRatio.multiply(it.orderedAmount)
                if (counterCurrencyAmountToUseLeft <= BigDecimal.ZERO) {
                    return@loop
                }
            }
        }
        return if (counterCurrencyAmountToUseLeft <= BigDecimal.ZERO) {
            return OrderBookAveragePrice(
                    averagePrice = counterCurrencyAmount.divide(sumAmountBaseCurrrencyUsed, HALF_EVEN).setScale(8, HALF_EVEN),
                    baseCurrencyAmount = sumAmountBaseCurrrencyUsed.setScale(8, HALF_EVEN)
            )
        } else {
            null
        }
    }

}