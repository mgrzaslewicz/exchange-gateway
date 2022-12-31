package automate.profit.autocoin.spi.exchange.order.service.authorized

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier

interface AuthorizedOrderServiceFactory<T> {

    fun createAuthorizedOrderService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedOrderService<T>

}
