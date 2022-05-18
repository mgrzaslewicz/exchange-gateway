package automate.profit.autocoin.order

data class OrderResponseDto(
    val exchangeUserId: String,
    val exchangeId: String,
    val exchangeName: String,
    val orderId: String,
    val baseCurrencyCode: String,
    val counterCurrencyCode: String,
    val orderType: String,
    val orderStatus: String?,
    val orderedAmount: Double,
    val filledAmount: Double?,
    val price: Double,
    val receivedAtMillis: Long,
    val exchangeTimestampMillis: Long?
)
