package automate.profit.autocoin.exchange.currency

import java.math.BigDecimal

data class CurrencyBalance(
    val currencyCode: String,
    val available: BigDecimal,
    val total: BigDecimal,
    val frozen: BigDecimal
)
