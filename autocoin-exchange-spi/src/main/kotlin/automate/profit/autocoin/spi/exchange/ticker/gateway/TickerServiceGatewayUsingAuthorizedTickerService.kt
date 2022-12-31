package automate.profit.autocoin.spi.exchange.ticker.gateway

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.ticker.Ticker
import automate.profit.autocoin.spi.exchange.ticker.service.authorized.AuthorizedTickerServiceFactory
import java.util.function.Supplier

class TickerServiceGatewayUsingAuthorizedTickerService(
    private val authorizedTickerServiceFactory: AuthorizedTickerServiceFactory,
) : TickerServiceGateway {

    override fun getTicker(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>?, currencyPair: CurrencyPair): Ticker {
        return authorizedTickerServiceFactory.createAuthorizedTickerService(
            exchangeName = exchangeName,
            apiKey = apiKey,
        ).getTicker(currencyPair = currencyPair)
    }

    override fun getTickers(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>?, currencyPairs: Collection<CurrencyPair>): List<Ticker> {
        return authorizedTickerServiceFactory.createAuthorizedTickerService(
            exchangeName = exchangeName,
            apiKey = apiKey,
        ).getTickers(currencyPairs = currencyPairs)
    }

}
