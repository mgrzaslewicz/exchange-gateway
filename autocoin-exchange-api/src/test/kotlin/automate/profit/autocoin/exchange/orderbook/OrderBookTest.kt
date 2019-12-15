package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.order.ExchangeOrderType.BID_BUY
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
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

    private val xtzBtcCurrencyPair = CurrencyPair.of("XTZ/BTC")
    private val gateioOrderBook = OrderBook(
            sellOrders = emptyList(),
            buyOrders = listOf(
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("155.742"),
                            price = BigDecimal("0.00023049"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("302.10567656"),
                            price = BigDecimal("0.00023048"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("1000.0"),
                            price = BigDecimal("0.00023039"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("267.64"),
                            price = BigDecimal("0.00023035"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("632.0"),
                            price = BigDecimal("0.00023015"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("119.9109"),
                            price = BigDecimal("0.00023004"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("335.0146"),
                            price = BigDecimal("0.00022894"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("6117.0"),
                            price = BigDecimal("0.00022893"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("477.0"),
                            price = BigDecimal("0.00022891"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("1228.0723365"),
                            price = BigDecimal("0.00022839"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("2478.0"),
                            price = BigDecimal("0.00022725"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("128.515"),
                            price = BigDecimal("0.000225"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("1290.0"),
                            price = BigDecimal("0.00022499"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("1245.0"),
                            price = BigDecimal("0.00022442"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("66.0"),
                            price = BigDecimal("0.00022272"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("250.0"),
                            price = BigDecimal("0.00022056"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("93.139"),
                            price = BigDecimal("0.0002201"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("4858.0"),
                            price = BigDecimal("0.00022"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("250.0"),
                            price = BigDecimal("0.00021056"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("83.246"),
                            price = BigDecimal("0.00020241"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("15.0"),
                            price = BigDecimal("0.00020142"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("81.0"),
                            price = BigDecimal("0.00020042"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("61.999"),
                            price = BigDecimal("0.0002"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("130.208"),
                            price = BigDecimal("0.000192"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("500.0"),
                            price = BigDecimal("0.00019156"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("199.346"),
                            price = BigDecimal("0.00018985"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("2494.065"),
                            price = BigDecimal("0.00018924"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("901.587"),
                            price = BigDecimal("0.000189"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("2.871"),
                            price = BigDecimal("0.0001851"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    ),
                    OrderBookExchangeOrder(
                            exchangeName = "gateio",
                            type = BID_BUY,
                            orderedAmount = BigDecimal("40233.691"),
                            price = BigDecimal("0.000185"),
                            currencyPair = xtzBtcCurrencyPair,
                            timestamp = timestampDoesNotMatter
                    )
            )
    )
    private val sampleOrders = listOf(
            OrderBookExchangeOrder(
                    exchangeName = "exchangeA",
                    type = BID_BUY,
                    orderedAmount = 10.toBigDecimal(),
                    price = 1.5.toBigDecimal(),
                    currencyPair = currencyPair,
                    timestamp = timestampDoesNotMatter
            ),
            OrderBookExchangeOrder(
                    exchangeName = "exchangeA",
                    type = BID_BUY,
                    orderedAmount = 15.toBigDecimal(),
                    price = 1.45.toBigDecimal(),
                    currencyPair = currencyPair,
                    timestamp = timestampDoesNotMatter
            ),
            OrderBookExchangeOrder(
                    exchangeName = "exchangeA",
                    type = BID_BUY,
                    orderedAmount = 30.toBigDecimal(),
                    price = 1.40.toBigDecimal(),
                    currencyPair = currencyPair,
                    timestamp = timestampDoesNotMatter
            ),
            OrderBookExchangeOrder(
                    exchangeName = "exchangeA",
                    type = BID_BUY,
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
        "1.48333333, 10.0, 222.5", // 22.25 units
        "1.41533333, 10.0, 1061.5", // 106.15 units, whole order book
        "null, 10.0, 1061.6" // more than in order book
    ])
    fun shouldCalculateWeightedAverageBuyPriceInUsd(@ConvertWith(NullableConverter::class) expectedAveragePrice: BigDecimal?, usdPrice: BigDecimal, usdAmount: BigDecimal) {
        val orderBook = OrderBook(buyOrders = sampleOrders, sellOrders = emptyList())
        assertThat(orderBook.getWeightedAverageBuyPrice(usdAmount, usdPrice)).isEqualTo(expectedAveragePrice)
    }

    @Test
    fun testGateioOrderBookAveragePrice() {
        val avgPriceHavingUsd = gateioOrderBook.getWeightedAverageBuyPrice(BigDecimal(200.0), BigDecimal(7150.31))
        assertThat(avgPriceHavingUsd).isEqualTo(BigDecimal("0.00023049"))
    }

}