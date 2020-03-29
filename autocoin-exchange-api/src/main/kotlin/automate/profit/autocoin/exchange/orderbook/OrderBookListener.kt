package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair

interface OrderBookListener {

    fun onOrderBook(exchange: SupportedExchange, currencyPair: CurrencyPair, orderBook: OrderBook)

    /**
     * There was no orderBook change on exchange but time has passed
     * @param orderBook might be the same that was already fetched from exchange or none
     */
    fun onNoNewOrderBook(exchange: SupportedExchange, currencyPair: CurrencyPair, orderBook: OrderBook?) {}

}
