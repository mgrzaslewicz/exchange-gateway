package automate.profit.autocoin.spi.exchange

import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier

interface AuthorizedService<T> {
    val exchangeName: ExchangeName

    val apiKey: ApiKeySupplier<T>
}
