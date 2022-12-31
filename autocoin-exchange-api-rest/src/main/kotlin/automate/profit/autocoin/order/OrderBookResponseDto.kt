package automate.profit.autocoin.order

import automate.profit.autocoin.exchange.orderbook.OrderBook

data class OrderBookResponseDto(
        val exchangeName: String,
        val buyOrders: List<OrderBookExchangeOrderDto> = emptyList(),
        val sellOrders: List<OrderBookExchangeOrderDto> = emptyList(),
        val errorMessage: String? = null
) {
    fun toOrderBook() = OrderBook(
            buyOrders = buyOrders.map { it.toOrderBookExchangeOrder() },
            sellOrders = sellOrders.map { it.toOrderBookExchangeOrder() }
    )
}