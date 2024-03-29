package com.autocoin.exchangegateway.dto.exchange.order

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.api.exchange.order.OpenOrders
import com.autocoin.exchangegateway.api.exchange.order.Order
import com.autocoin.exchangegateway.dto.order.toDto
import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide
import com.autocoin.exchangegateway.spi.exchange.order.OrderStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OpenOrdersDtoTest {
    private val exchange = object : Exchange {
        override val exchangeName = "test"
    }
    private val order1 = Order(
        exchange = exchange,
        side = OrderSide.BID_BUY,
        currencyPair = CurrencyPair.of("BTC/USDT"),
        orderedAmount = 45.678.toBigDecimal(),
        filledAmount = 15.345.toBigDecimal(),
        price = 12.0.toBigDecimal(),
        status = OrderStatus.PARTIALLY_FILLED,
        receivedAtMillis = 123L,
        exchangeTimestampMillis = null,
        exchangeOrderId = "1-2-3",
    )
    private val openOrders = OpenOrders(
        exchangeUserId = "user1",
        openOrders = listOf(order1),
        errorMessage = null,
    )
    private val dto = openOrders.toDto()

    @Test
    fun shouldConvertToDtoAndBack() {
        // when
        val fromDto = dto.toOpenOrders { exchange }
        // then
        assertThat(fromDto).isEqualTo(openOrders)
    }

}
