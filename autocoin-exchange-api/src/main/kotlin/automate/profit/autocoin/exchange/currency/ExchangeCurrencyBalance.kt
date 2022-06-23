package automate.profit.autocoin.exchange.currency

import java.math.BigDecimal

data class ExchangeCurrencyBalance(
    val currencyCode: String,
    val amountAvailable: BigDecimal,
    val totalAmount: BigDecimal,
    val amountInOrders: BigDecimal,
)
