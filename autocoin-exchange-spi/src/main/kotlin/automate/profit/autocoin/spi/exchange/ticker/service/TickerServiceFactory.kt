package automate.profit.autocoin.spi.exchange.ticker.service

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier

interface TickerServiceFactory<T> {
    fun createTickerService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): TickerService<T>

}
