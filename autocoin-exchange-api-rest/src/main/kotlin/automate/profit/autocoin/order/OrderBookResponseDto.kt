package automate.profit.autocoin.order

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.orderbook.OrderBook

data class OrderBookResponseDto(
        val exchangeName: String,
        /**
         * A/B format
         */
        val currencyPair: String,
        val buyOrders: List<OrderBookExchangeOrderDto> = emptyList(),
        val sellOrders: List<OrderBookExchangeOrderDto> = emptyList(),
        val errorMessage: String? = null
) {
    fun toOrderBook() = OrderBook(
            buyOrders = buyOrders.map { it.toOrderBookExchangeOrder() },
            sellOrders = sellOrders.map { it.toOrderBookExchangeOrder() }
    )
}

fun OrderBook.toDto(exchange: SupportedExchange, currencyPair: CurrencyPair) =
        OrderBookResponseDto(
                exchangeName = exchange.exchangeName,
                currencyPair = currencyPair.toString(),
                buyOrders = buyOrders.map { it.toOrderBookExchangeOrderDto() },
                sellOrders = sellOrders.map { it.toOrderBookExchangeOrderDto() }
        )