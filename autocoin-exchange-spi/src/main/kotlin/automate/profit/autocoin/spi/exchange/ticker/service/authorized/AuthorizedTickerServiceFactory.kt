package automate.profit.autocoin.spi.exchange.ticker.service.authorized

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier

interface AuthorizedTickerServiceFactory<T> {
    fun createAuthorizedTickerService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedTickerService<T>
}
