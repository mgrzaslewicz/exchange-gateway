package automate.profit.autocoin.spi.exchange.orderbook.service.authorized

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import java.util.function.Supplier

interface AuthorizedOrderBookServiceFactory {
    fun createAuthorizedOrderBookService(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>?): AuthorizedOrderBookService
}
