package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.SupportedExchange
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import org.knowm.xchange.dto.marketdata.OrderBook as XchangeOrderBook

class OrderBookTest {

    @Test
    fun shouldConvertToOrderBookWhenNullTimestamp() {
        // given
        val receivedAtMillis = 456L
        val xchangeOrderBook = XchangeOrderBook(null, emptyList(), emptyList())
        // when
        val orderBook = xchangeOrderBook.toOrderBook(SupportedExchange.BITTREX.exchangeName, receivedAtMillis)
        // then
        SoftAssertions().apply {
            assertThat(orderBook.buyOrders).isEmpty()
            assertThat(orderBook.sellOrders).isEmpty()
            assertThat(orderBook.exchangeTimestampMillis).isNull()
            assertThat(orderBook.receivedAtMillis).isEqualTo(receivedAtMillis)
            assertAll()
        }
    }

}
