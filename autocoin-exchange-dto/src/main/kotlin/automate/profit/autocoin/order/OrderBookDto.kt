package automate.profit.autocoin.order

import automate.profit.autocoin.SerializableToJson
import automate.profit.autocoin.api.exchange.currency.CurrencyPair
import automate.profit.autocoin.api.exchange.orderbook.OrderBook
import automate.profit.autocoin.appendNullable
import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.orderbook.OrderBook as SpiOrderBook


data class OrderBookDto(
    val exchangeName: String,
    /**
     * A/B format
     */
    val currencyPair: String,
    val buyOrders: List<OrderInOrderBookDto> = emptyList(),
    val sellOrders: List<OrderInOrderBookDto> = emptyList(),

    val receivedAtMillis: Long,
    val exchangeTimestampMillis: Long?,

    val errorMessage: String? = null,
) : SerializableToJson {
    fun toOrderBook(): SpiOrderBook = OrderBook(
        exchangeName = ExchangeName(exchangeName),
        buyOrders = buyOrders.map { it.toOrderInOrderBook() },
        sellOrders = sellOrders.map { it.toOrderInOrderBook() },
        receivedAtMillis = receivedAtMillis,
        exchangeTimestampMillis = exchangeTimestampMillis,
        currencyPair = CurrencyPair.of(currencyPair),
    )

    override fun appendJson(builder: StringBuilder) = builder
        .append("{")
        .append("\"exchangeName\":\"$exchangeName\",")
        .append("\"currencyPair\":\"$currencyPair\",")
        .append("\"buyOrders\":[")
        .append(buyOrders.joinToString(",") { it.toJson() })
        .append("],")
        .append("\"sellOrders\":[")
        .append(sellOrders.joinToString(",") { it.toJson() })
        .append("],")
        .append("\"receivedAtMillis\":$receivedAtMillis,")
        .append("\"exchangeTimestampMillis\":$exchangeTimestampMillis,")
        .append("\"errorMessage\":")
        .appendNullable(errorMessage)
        .append("}")
}

fun OrderBook.toDto() =
    OrderBookDto(
        exchangeName = exchangeName.value,
        currencyPair = currencyPair.toString(),
        buyOrders = buyOrders
            .sortedByDescending { it.price }
            .map { it.toDto() },
        sellOrders = sellOrders
            .sortedBy { it.price }
            .map { it.toDto() },
        receivedAtMillis = receivedAtMillis,
        exchangeTimestampMillis = exchangeTimestampMillis,
    )

