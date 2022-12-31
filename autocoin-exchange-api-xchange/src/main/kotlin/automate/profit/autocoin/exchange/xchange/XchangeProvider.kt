package automate.profit.autocoin.exchange.xchange

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier
import org.knowm.xchange.Exchange


interface XchangeProvider<T> {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): Exchange
}




