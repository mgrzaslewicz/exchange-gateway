package automate.profit.autocoin.spi.exchange.orderbook.gateway

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.orderbook.OrderBook

class DelegateOrderBookServiceGateway(
    private val orderBookServiceGateways: Map<ExchangeName, OrderBookServiceGateway>,
) : OrderBookServiceGateway {
    override fun getOrderBook(exchangeName: ExchangeName, currencyPair: CurrencyPair): OrderBook {
        return orderBookServiceGateways.getValue(exchangeName).getOrderBook(
            exchangeName = exchangeName,
            currencyPair = currencyPair,
        )
    }
}
