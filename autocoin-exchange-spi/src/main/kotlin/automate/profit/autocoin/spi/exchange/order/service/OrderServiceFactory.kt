package automate.profit.autocoin.spi.exchange.order.service

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier

interface OrderServiceFactory<T> {

    fun createOrderService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): OrderService<T>

}
