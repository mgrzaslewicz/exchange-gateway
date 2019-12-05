package automate.profit.autocoin.exchange.order

import automate.profit.autocoin.exchange.currency.CurrencyPair
import java.math.BigDecimal
import java.time.Instant

data class ExchangeOrder(
        val exchangeName: String,
        /** id at external exchange **/
        val orderId: String,
        val type: ExchangeOrderType,
        val orderedAmount: BigDecimal,
        val filledAmount: BigDecimal?,
        val price: BigDecimal,
        val currencyPair: CurrencyPair,
        val status: ExchangeOrderStatus,
        val timestamp: Instant?
)
