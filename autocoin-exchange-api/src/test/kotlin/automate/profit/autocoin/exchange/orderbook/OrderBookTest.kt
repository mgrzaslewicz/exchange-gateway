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

    @ParameterizedTest(name = "Weighted average price should be {0} for amount {1}")
    @CsvSource(*[
        "1.50000000, 9.0",
        "1.50000000, 10.0",
        /* 'avg = (10.0 * 1.5 + 5 * 1.45) / 15.0 = 1.48(3)' */
        "1.48333333, 15.0",
        /* whole order book */
        "1.41533333, 75",
        /* more than in order book */
        "null, 76"
    ])
    fun shouldCalculateWeightedAveragePrice(@ConvertWith(NullableConverter::class) expectedWeightedAveragePrice: BigDecimal?, amount: BigDecimal) {
        val orderBook = OrderBook(buyOrders = sampleOrders, sellOrders = sampleOrders)
        assertThat(orderBook.getWeightedAverageBuyPrice(amount)).isEqualTo(expectedWeightedAveragePrice)
        assertThat(orderBook.getWeightedAverageSellPrice(amount)).isEqualTo(expectedWeightedAveragePrice)
    }

    @ParameterizedTest(name = "Weighted average USD price should be {0} for USD price {1} and amount {2}")
    @CsvSource(*[
        "2.00000000, 0.75, 2.0",
        "1.97481481, 0.75, 15.0",
        "1.91407408, 0.75, 45.0",
        "1.89600000, 0.75, 60.0",
        /* more than in order book */
        "null, 0.75, 80.0"
    ])
    fun shouldCalculateWeightedAverageBuyPriceInUsd(@ConvertWith(NullableConverter::class) expectedAverageUsdPrice: BigDecimal?, usdPrice: BigDecimal, usdAmount: BigDecimal) {
        val orderBook = OrderBook(buyOrders = sampleOrders, sellOrders = sampleOrders)
        assertThat(orderBook.getWeightedAverageBuyPriceInOtherCurrency(usdAmount, usdPrice)).isEqualTo(expectedAverageUsdPrice)
        assertThat(orderBook.getWeightedAverageSellPriceInOtherCurrency(usdAmount, usdPrice)).isEqualTo(expectedAverageUsdPrice)
    }
}