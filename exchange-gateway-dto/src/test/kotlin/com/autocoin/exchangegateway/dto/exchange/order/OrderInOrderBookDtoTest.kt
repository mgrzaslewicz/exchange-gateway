package com.autocoin.exchangegateway.dto.exchange.order

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.api.exchange.orderbook.OrderInOrderBook
import com.autocoin.exchangegateway.dto.TestObjectMapper
import com.autocoin.exchangegateway.dto.order.toDto
import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrderInOrderBookDtoTest {
    private val orderInOrderBook = OrderInOrderBook(
        exchangeName = ExchangeName("exchange1"),
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
        val fromDto = dto.toOrderInOrderBook()
        // then
        assertThat(fromDto).isEqualTo(orderInOrderBook)
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
