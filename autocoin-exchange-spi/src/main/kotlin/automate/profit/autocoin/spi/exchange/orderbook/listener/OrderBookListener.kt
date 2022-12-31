package automate.profit.autocoin.spi.exchange.orderbook.listener

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.orderbook.OrderBook

interface OrderBookListener {

    fun onOrderBook(exchangeName: ExchangeName, currencyPair: CurrencyPair, orderBook: OrderBook)

    /**
     * There was no orderBook change on exchange but time has passed
     * @param orderBook might be the same that was already fetched from exchange or none
     */
    fun onNoNewOrderBook(exchange: ExchangeName, currencyPair: CurrencyPair, orderBook: OrderBook?) {}

}
