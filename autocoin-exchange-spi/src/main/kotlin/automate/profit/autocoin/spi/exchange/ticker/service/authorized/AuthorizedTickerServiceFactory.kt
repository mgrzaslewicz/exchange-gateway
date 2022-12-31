package automate.profit.autocoin.spi.exchange.ticker.service.authorized

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import java.util.function.Supplier

interface AuthorizedTickerServiceFactory {
    fun createAuthorizedTickerService(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>?,
    ): AuthorizedTickerService
}
