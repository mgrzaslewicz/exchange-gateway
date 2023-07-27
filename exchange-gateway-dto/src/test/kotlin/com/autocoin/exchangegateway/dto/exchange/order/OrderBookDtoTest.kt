package com.autocoin.exchangegateway.dto.exchange.order

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.api.exchange.orderbook.OrderBook
import com.autocoin.exchangegateway.api.exchange.orderbook.OrderInOrderBook
import com.autocoin.exchangegateway.dto.order.toDto
import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrderBookDtoTest {
    private val exchange = object : Exchange {
        override val exchangeName = "test"
    }
    private val orderInOrderBook = OrderInOrderBook(
        exchange = exchange,
        side = OrderSide.BID_BUY,
        orderedAmount = 45.678.toBigDecimal(),
        price = 12.0.toBigDecimal(),
        currencyPair = CurrencyPair.of("BTC/USDT"),
        receivedAtMillis = 123L,
        exchangeTimestampMillis = null,
    )
    private val orderBook = OrderBook(
        exchange = exchange,
        currencyPair = CurrencyPair.of("BTC/USDT"),
        buyOrders = listOf(orderInOrderBook),
        sellOrders = listOf(orderInOrderBook.copy(side = OrderSide.ASK_SELL)),
        receivedAtMillis = 123L,
        exchangeTimestampMillis = 456L,
    )
    private val dto = orderBook.toDto()

    @Test
    fun shouldConvertToDtoAndBack() {
        // when
        val fromDto = dto.toOrderBook { exchange }
        // then
        assertThat(fromDto).isEqualTo(orderBook)
    }

}
