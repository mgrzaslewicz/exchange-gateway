package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.order.ExchangeOrderType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.converter.ArgumentConversionException
import org.junit.jupiter.params.converter.ConvertWith
import org.junit.jupiter.params.converter.DefaultArgumentConverter
import org.junit.jupiter.params.converter.SimpleArgumentConverter
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigDecimal
import java.time.Instant


class OrderBookTest {
    private val currencyPair = CurrencyPair.of("currencyX/currencyY")
    private val timestampDoesNotMatter: Instant? = null
    private val sampleOrders = listOf(
            OrderBookExchangeOrder(
                    exchangeName = "exchangeA",
                    type = ExchangeOrderType.BID_BUY,
                    orderedAmount = 10.toBigDecimal(),
                    price = 1.5.toBigDecimal(),
                    currencyPair = currencyPair,
                    timestamp = timestampDoesNotMatter
            ),
            OrderBookExchangeOrder(
                    exchangeName = "exchangeA",
                    type = ExchangeOrderType.BID_BUY,
                    orderedAmount = 15.toBigDecimal(),
                    price = 1.45.toBigDecimal(),
                    currencyPair = currencyPair,
                    timestamp = timestampDoesNotMatter
            ),
            OrderBookExchangeOrder(
                    exchangeName = "exchangeA",
                    type = ExchangeOrderType.BID_BUY,
                    orderedAmount = 30.toBigDecimal(),
                    price = 1.40.toBigDecimal(),
                    currencyPair = currencyPair,
                    timestamp = timestampDoesNotMatter
            ),
            OrderBookExchangeOrder(
                    exchangeName = "exchangeA",
                    type = ExchangeOrderType.BID_BUY,
                    orderedAmount = 20.toBigDecimal(),
                    price = 1.37.toBigDecimal(),
                    currencyPair = currencyPair,
                    timestamp = timestampDoesNotMatter
            )
    )

    private class NullableConverter : SimpleArgumentConverter() {
        @Throws(ArgumentConversionException::class)
        override fun convert(source: Any, targetType: Class<*>?): Any? {
            return if ("null" == source) {
                null
            } else DefaultArgumentConverter.INSTANCE.convert(source, targetType)
        }
    }

    @ParameterizedTest(name = "Weighted average of currencyX price should be {0} for amount {1}")
    @CsvSource(*[
        "1.50000000, 9.0",
        "1.50000000, 10.0",
        "1.48333333, 15.0", // 'avg = (10.0 * 1.5 + 5 * 1.45) / 15.0 = 1.48(3)'
        "1.41533333, 75", // whole order book
        "null, 76" // more than in order book
    ])
    fun shouldCalculateWeightedAveragePrice(@ConvertWith(NullableConverter::class) expectedWeightedAveragePrice: BigDecimal?, amount: BigDecimal) {
        val orderBook = OrderBook(buyOrders = sampleOrders, sellOrders = emptyList())
        assertThat(orderBook.getWeightedAverageBuyPrice(amount)).isEqualTo(expectedWeightedAveragePrice)
    }

    @ParameterizedTest(name = "Weighted average of currencyX price should be {0} for USD price {1} and amount {2}")
    @CsvSource(*[
        "1.50000000, 10.0, 90.0", // 90.0 / 10.0 = 9 units of currencyY, value of first order is 15.0 currencyY
        "1.50000000, 8.0, 120.0", // 15 units of currencyY
        "1.48341014, 10.0, 222.5", // 22.25 units
        "1.41533333, 10.0, 1061.49", // 106.149 units, whole order book is 106.15
        "null, 10.0, 1070.0" // 107 units, more than in order book
    ])
    fun shouldCalculateWeightedAverageBuyPriceInUsd(@ConvertWith(NullableConverter::class) expectedAveragePrice: BigDecimal?, usdPrice: BigDecimal, usdAmount: BigDecimal) {
        val orderBook = OrderBook(buyOrders = sampleOrders, sellOrders = emptyList())
        assertThat(orderBook.getWeightedAverageBuyPrice(usdAmount, usdPrice)).isEqualTo(expectedAveragePrice)
    }
}