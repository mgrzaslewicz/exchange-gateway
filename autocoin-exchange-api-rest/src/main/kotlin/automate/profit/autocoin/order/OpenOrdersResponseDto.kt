package automate.profit.autocoin.order

data class OpenOrdersResponseDto(
    val exchangeName: String,
    val exchangeUserId: String,
    val openOrders: List<OrderResponseDto>,
    val errorMessage: String?
)
