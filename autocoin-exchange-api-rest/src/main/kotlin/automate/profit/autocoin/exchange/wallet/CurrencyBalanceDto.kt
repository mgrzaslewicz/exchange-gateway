package automate.profit.autocoin.exchange.wallet

data class CurrencyBalanceDto(
    val currencyCode: String,
    val available: Double,
    val total: Double,
    val frozen: Double
)
