package automate.profit.autocoin.spi.exchange.order.service

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import java.util.function.Supplier

interface OrderServiceFactory {

    fun createOrderService(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
    ): OrderService

}
