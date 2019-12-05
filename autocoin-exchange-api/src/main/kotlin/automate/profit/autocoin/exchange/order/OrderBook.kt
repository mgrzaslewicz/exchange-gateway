package automate.profit.autocoin.exchange.order

data class OrderBook(
        val buyOrders: List<OrderBookExchangeOrder>,
        val sellOrders: List<OrderBookExchangeOrder>
)