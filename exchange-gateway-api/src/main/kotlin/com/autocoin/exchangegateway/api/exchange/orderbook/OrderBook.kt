package com.autocoin.exchangegateway.api.exchange.orderbook

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import java.math.BigDecimal
import java.math.RoundingMode.HALF_EVEN
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderBook as SpiOrderBook
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderBookAveragePrice as SpiOrderBookAveragePrice


data class OrderBook(
    override val exchangeName: ExchangeName,
    override val currencyPair: CurrencyPair,
    override val buyOrders: List<OrderInOrderBook>,
    override val sellOrders: List<OrderInOrderBook>,
    override val receivedAtMillis: Long,
    override val exchangeTimestampMillis: Long?,
) : SpiOrderBook {

    override fun deepEquals(other: SpiOrderBook): Boolean {
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

    override fun buyOrderBookValueInCounterCurrency(): BigDecimal = buyOrders.fold(BigDecimal.ZERO) { acc, order -> acc + order.valueInCounterCurrency() }

    override fun getWeightedAverageBuyPrice(baseCurrencyAmount: BigDecimal): SpiOrderBookAveragePrice? {
        return getWeightedAveragePrice(baseCurrencyAmount, buyOrders)
    }

    override fun getWeightedAverageSellPrice(baseCurrencyAmount: BigDecimal): SpiOrderBookAveragePrice? {
        return getWeightedAveragePrice(baseCurrencyAmount, sellOrders)
    }

    private fun getWeightedAveragePrice(
        baseCurrencyAmount: BigDecimal,
        orders: List<OrderInOrderBook>,
    ): SpiOrderBookAveragePrice? {
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
                baseCurrencyAmount = baseCurrencyAmount.setScale(8, HALF_EVEN),
            )
        }
        else {
            null
        }
    }

    override fun getWeightedAverageBuyPrice(
        otherCurrencyAmount: BigDecimal,
        otherCurrencyPrice: BigDecimal,
    ): SpiOrderBookAveragePrice? {
        val counterCurrencyAmountToSpend = otherCurrencyAmount.setScale(16, HALF_EVEN).divide(otherCurrencyPrice.setScale(16, HALF_EVEN), HALF_EVEN)
        return getWeightedAveragePriceWithCounterCurrencyAmount(counterCurrencyAmountToSpend, buyOrders)
    }

    override fun getWeightedAverageSellPrice(
        otherCurrencyAmount: BigDecimal,
        otherCurrencyPrice: BigDecimal,
    ): SpiOrderBookAveragePrice? {
        val counterCurrencyAmountToSpend = otherCurrencyAmount.setScale(16, HALF_EVEN).divide(otherCurrencyPrice.setScale(16, HALF_EVEN), HALF_EVEN)
        return getWeightedAveragePriceWithCounterCurrencyAmount(counterCurrencyAmountToSpend, sellOrders)
    }

    private fun getWeightedAverageBuyPrice(
        otherCurrencyAmount: BigDecimal,
        otherCurrencyPrice: BigDecimal,
        orders: List<OrderInOrderBook>,
    ): SpiOrderBookAveragePrice? {
        val counterCurrencyAmountToSpend = otherCurrencyAmount.setScale(16, HALF_EVEN).divide(otherCurrencyPrice.setScale(16, HALF_EVEN), HALF_EVEN)
        return getWeightedAveragePriceWithCounterCurrencyAmount(counterCurrencyAmountToSpend, orders)
    }

    private fun getWeightedAveragePriceWithCounterCurrencyAmount(
        counterCurrencyAmount: BigDecimal,
        orders: List<OrderInOrderBook>,
    ): SpiOrderBookAveragePrice? {
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
                baseCurrencyAmount = sumAmountBaseCurrrencyUsed.setScale(8, HALF_EVEN),
            )
        }
        else {
            null
        }
    }

}
