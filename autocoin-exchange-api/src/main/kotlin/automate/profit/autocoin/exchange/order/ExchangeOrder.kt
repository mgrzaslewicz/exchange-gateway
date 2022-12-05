package automate.profit.autocoin.exchange.order

import automate.profit.autocoin.exchange.currency.CurrencyPair
import java.math.BigDecimal

data class ExchangeOrder(
    val exchangeName: String,
    val exchangeOrderId: String,
    val type: ExchangeOrderType,
    val orderedAmount: BigDecimal,
    val filledAmount: BigDecimal?,
    val price: BigDecimal,
    val currencyPair: CurrencyPair,
    val status: ExchangeOrderStatus,
    val receivedAtMillis: Long,
    val exchangeTimestampMillis: Long?,
)
