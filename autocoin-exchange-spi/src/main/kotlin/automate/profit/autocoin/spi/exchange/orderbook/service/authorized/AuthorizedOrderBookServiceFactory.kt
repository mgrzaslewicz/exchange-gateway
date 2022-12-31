package automate.profit.autocoin.spi.exchange.orderbook.service.authorized

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier

interface AuthorizedOrderBookServiceFactory<T> {
    fun createAuthorizedOrderBookService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedOrderBookService<T>
}
