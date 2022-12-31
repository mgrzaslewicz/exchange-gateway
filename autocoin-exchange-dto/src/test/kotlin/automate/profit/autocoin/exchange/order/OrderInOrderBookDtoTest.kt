package automate.profit.autocoin.exchange.order

import automate.profit.autocoin.TestObjectMapper
import automate.profit.autocoin.api.exchange.currency.CurrencyPair
import automate.profit.autocoin.api.exchange.orderbook.OrderInOrderBook
import automate.profit.autocoin.order.toDto
import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.order.OrderSide
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
