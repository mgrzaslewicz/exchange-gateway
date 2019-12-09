package automate.profit.autocoin.exchange.orderbook

data class OrderBook(
        val buyOrders: List<OrderBookExchangeOrder>,
        val sellOrders: List<OrderBookExchangeOrder>
) {
//    fun getWeightedAverageSellPriceForDepth()
}