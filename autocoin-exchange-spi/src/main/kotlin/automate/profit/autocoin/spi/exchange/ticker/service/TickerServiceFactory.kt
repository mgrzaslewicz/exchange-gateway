package automate.profit.autocoin.spi.exchange.ticker.service

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import java.util.function.Supplier

interface TickerServiceFactory {
    fun createTickerService(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>?,
    ): TickerService

}
