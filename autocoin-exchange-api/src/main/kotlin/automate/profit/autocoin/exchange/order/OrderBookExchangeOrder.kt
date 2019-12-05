package automate.profit.autocoin.exchange.order

import automate.profit.autocoin.exchange.currency.CurrencyPair
import java.math.BigDecimal
import java.time.Instant

data class OrderBookExchangeOrder(
        val exchangeName: String,
        val type: ExchangeOrderType,
        val orderedAmount: BigDecimal,
        val price: BigDecimal,
        val currencyPair: CurrencyPair,
        val timestamp: Instant?
)