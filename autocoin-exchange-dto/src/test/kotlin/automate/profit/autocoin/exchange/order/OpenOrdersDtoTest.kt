package automate.profit.autocoin.exchange.order

import automate.profit.autocoin.TestObjectMapper
import automate.profit.autocoin.api.exchange.currency.CurrencyPair
import automate.profit.autocoin.api.exchange.order.OpenOrders
import automate.profit.autocoin.api.exchange.order.Order
import automate.profit.autocoin.order.toDto
import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.order.OrderSide
import automate.profit.autocoin.spi.exchange.order.OrderStatus
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
        exchangeName = ExchangeName("exchange1"),
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
