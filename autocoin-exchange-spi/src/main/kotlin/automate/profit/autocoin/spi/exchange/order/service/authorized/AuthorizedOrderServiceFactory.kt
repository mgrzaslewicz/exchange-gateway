package automate.profit.autocoin.spi.exchange.order.service.authorized

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import java.util.function.Supplier

interface AuthorizedOrderServiceFactory {

    fun createAuthorizedOrderService(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
    ): AuthorizedOrderService

}
