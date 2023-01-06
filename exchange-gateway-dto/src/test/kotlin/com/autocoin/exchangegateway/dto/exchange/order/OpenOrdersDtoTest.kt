package com.autocoin.exchangegateway.dto.exchange.order

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.api.exchange.order.OpenOrders
import com.autocoin.exchangegateway.api.exchange.order.Order
import com.autocoin.exchangegateway.dto.TestObjectMapper
import com.autocoin.exchangegateway.dto.order.toDto
import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide
import com.autocoin.exchangegateway.spi.exchange.order.OrderStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OpenOrdersDtoTest {
    private val order1 = Order(
        exchangeName = ExchangeName("exchange1"),
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
        val fromDto = dto.toOpenOrders()
        // then
        assertThat(fromDto).isEqualTo(openOrders)
    }

    @Test
    fun shouldSerializeToJson() {
        // given
        val objectMapper = TestObjectMapper().createObjectMapper()
        // when
        val json = dto.toJson()
        // then
        assertThat(json).isEqualTo(objectMapper.writeValueAsString(dto))
    }

}
