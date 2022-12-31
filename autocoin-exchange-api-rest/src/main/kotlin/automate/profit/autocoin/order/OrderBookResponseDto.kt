package automate.profit.autocoin.order

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.orderbook.OrderBook

data class OrderBookResponseDto(
    val exchangeName: String,
    /**
     * A/B format
     */
    val currencyPair: String,
    val buyOrders: List<OrderBookExchangeOrderDto> = emptyList(),
    val sellOrders: List<OrderBookExchangeOrderDto> = emptyList(),

    val receivedAtMillis: Long,
    val exchangeTimestampMillis: Long?,

    val errorMessage: String? = null
) {
    fun toOrderBook() = OrderBook(
        buyOrders = buyOrders.map { it.toOrderBookExchangeOrder() },
        sellOrders = sellOrders.map { it.toOrderBookExchangeOrder() },
        receivedAtMillis = receivedAtMillis,
        exchangeTimestampMillis = exchangeTimestampMillis,
    )
}

fun OrderBook.toDto(exchange: SupportedExchange, currencyPair: CurrencyPair) =
    OrderBookResponseDto(
        exchangeName = exchange.exchangeName,
        currencyPair = currencyPair.toString(),
        buyOrders = buyOrders
            .sortedByDescending { it.price }
            .map { it.toOrderBookExchangeOrderDto() },
        sellOrders = sellOrders
            .sortedBy { it.price }
            .map { it.toOrderBookExchangeOrderDto() },
        receivedAtMillis = receivedAtMillis,
        exchangeTimestampMillis = exchangeTimestampMillis,
    )
