package com.autocoin.exchangegateway.dto.exchange.order

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.api.exchange.orderbook.OrderInOrderBook
import com.autocoin.exchangegateway.dto.order.toDto
import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrderInOrderBookDtoTest {
    private val exchange = object : Exchange {
        override val exchangeName = "exchange"
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
    private val dto = orderInOrderBook.toDto()

    @Test
    fun shouldConvertToDtoAndBack() {
        // when
        val fromDto = dto.toOrderInOrderBook { exchange }
        // then
        assertThat(fromDto).isEqualTo(orderInOrderBook)
    }

}
