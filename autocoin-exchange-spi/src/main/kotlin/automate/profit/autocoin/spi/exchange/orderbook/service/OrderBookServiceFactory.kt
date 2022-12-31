package automate.profit.autocoin.spi.exchange.orderbook.service

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import java.util.function.Supplier

interface OrderBookServiceFactory {

    fun createOrderBookService(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey?>,
    ): OrderBookService

}
