package automate.profit.autocoin.order

data class OrderBookResponseDto(
        val exchangeName: String,
        val buyOrders: List<OrderBookExchangeOrderDto> = emptyList(),
        val sellOrders: List<OrderBookExchangeOrderDto> = emptyList(),
        val errorMessage: String? = null
)