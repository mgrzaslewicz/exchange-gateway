package automate.profit.autocoin.order

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.order.ExchangeOrderType
import automate.profit.autocoin.exchange.orderbook.OrderBookExchangeOrder
import java.math.BigDecimal

data class OrderBookExchangeOrderDto(
    val exchangeName: String,
    val type: ExchangeOrderType,
    val orderedAmount: Double,
    val price: String,
    val baseCurrency: String,
    val counterCurrency: String,
    val receivedAtMillis: Long,
    val exchangeTimestampMillis: Long?,
) {
    fun toOrderBookExchangeOrder() = OrderBookExchangeOrder(
        exchangeName = exchangeName,
        type = type,
        orderedAmount = orderedAmount.toBigDecimal(),
        price = BigDecimal(price),
        currencyPair = CurrencyPair.of(baseCurrency, counterCurrency),
        receivedAtMillis = receivedAtMillis,
        exchangeTimestampMillis = exchangeTimestampMillis,
    )
}

fun OrderBookExchangeOrder.toOrderBookExchangeOrderDto() = OrderBookExchangeOrderDto(
    exchangeName = exchangeName,
    type = type,
    orderedAmount = orderedAmount.toDouble(),
    price = price.toPlainString(),
    baseCurrency = currencyPair.base,
    counterCurrency = currencyPair.counter,
    receivedAtMillis = receivedAtMillis,
    exchangeTimestampMillis = exchangeTimestampMillis,
)
