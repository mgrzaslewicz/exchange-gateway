package automate.profit.autocoin.exchange.wallet

data class ExchangeCurrencyBalanceDto(
    val currencyCode: String,
    val amountAvailable: String,
    val totalAmount: String,
    val amountInOrders: String,
    val valueInOtherCurrency: Map<String, String?>? = null,
    val priceInOtherCurrency: Map<String, String?>? = null,
)
