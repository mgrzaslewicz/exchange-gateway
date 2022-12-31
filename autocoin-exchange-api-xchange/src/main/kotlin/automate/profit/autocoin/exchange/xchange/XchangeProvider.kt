package automate.profit.autocoin.exchange.xchange

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import org.knowm.xchange.Exchange
import java.util.function.Supplier


interface XchangeProvider {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>?,
    ): Exchange
}




