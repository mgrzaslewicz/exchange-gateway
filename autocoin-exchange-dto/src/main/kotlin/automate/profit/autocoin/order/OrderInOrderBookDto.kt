package automate.profit.autocoin.order

import automate.profit.autocoin.SerializableToJson
import automate.profit.autocoin.api.exchange.currency.CurrencyPair
import automate.profit.autocoin.api.exchange.orderbook.OrderInOrderBook
import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.order.OrderSide
import automate.profit.autocoin.spi.exchange.orderbook.OrderInOrderBook as SpiOrderInOrderBook

data class OrderInOrderBookDto(
    val exchangeName: String,
    val side: String,
    val orderedAmount: String,
    val price: String,
    val baseCurrency: String,
    val counterCurrency: String,
    val receivedAtMillis: Long,
    val exchangeTimestampMillis: Long?,
) : SerializableToJson {
    fun toOrderInOrderBook() = OrderInOrderBook(
        exchangeName = ExchangeName(exchangeName),
        side = OrderSide.valueOf(side),
        orderedAmount = orderedAmount.toBigDecimal(),
        price = price.toBigDecimal(),
        currencyPair = CurrencyPair.of(baseCurrency, counterCurrency),
        receivedAtMillis = receivedAtMillis,
        exchangeTimestampMillis = exchangeTimestampMillis,
    )

    override fun appendJson(builder: StringBuilder) = builder
        .append("{")
        .append("\"exchangeName\":\"$exchangeName\",")
        .append("\"side\":\"$side\",")
        .append("\"orderedAmount\":\"$orderedAmount\",")
        .append("\"price\":\"$price\",")
        .append("\"baseCurrency\":\"$baseCurrency\",")
        .append("\"counterCurrency\":\"$counterCurrency\",")
        .append("\"receivedAtMillis\":$receivedAtMillis,")
        .append("\"exchangeTimestampMillis\":$exchangeTimestampMillis")
        .append("}")
}

fun SpiOrderInOrderBook.toDto() = OrderInOrderBookDto(
    exchangeName = exchangeName.value,
    side = side.name,
    orderedAmount = orderedAmount.toPlainString(),
    price = price.toPlainString(),
    baseCurrency = currencyPair.base,
    counterCurrency = currencyPair.counter,
    receivedAtMillis = receivedAtMillis,
    exchangeTimestampMillis = exchangeTimestampMillis,
)

