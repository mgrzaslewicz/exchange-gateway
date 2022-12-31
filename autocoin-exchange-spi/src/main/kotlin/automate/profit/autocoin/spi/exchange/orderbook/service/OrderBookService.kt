package automate.profit.autocoin.spi.exchange.orderbook.service

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.orderbook.OrderBook
import java.util.function.Supplier

interface OrderBookService {
    val exchangeName: ExchangeName
    fun getOrderBook(apiKey: Supplier<ApiKey>?, currencyPair: CurrencyPair): OrderBook
}


