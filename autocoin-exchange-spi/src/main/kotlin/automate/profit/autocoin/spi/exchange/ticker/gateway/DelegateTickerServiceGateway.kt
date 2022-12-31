package automate.profit.autocoin.spi.exchange.ticker.gateway

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.ticker.Ticker
import automate.profit.autocoin.spi.exchange.ticker.service.TickerService
import java.util.function.Supplier

class DelegateTickerServiceGateway(
    private val tickerServiceGateways: Map<ExchangeName, TickerService>,
) : TickerServiceGateway {

    override fun getTicker(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>?,
        currencyPair: CurrencyPair,
    ): Ticker {
        return tickerServiceGateways.getValue(exchangeName).getTicker(
            apiKey = apiKey,
            currencyPair = currencyPair,
        )
    }

    override fun getTickers(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>?,
        currencyPairs: Collection<CurrencyPair>,
    ): List<Ticker> {
        return tickerServiceGateways.getValue(exchangeName).getTickers(
            apiKey = apiKey,
            currencyPairs = currencyPairs,
        )
    }
}
