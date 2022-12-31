package automate.profit.autocoin.spi.exchange.ticker.gateway

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.ticker.Ticker
import automate.profit.autocoin.spi.exchange.ticker.service.authorized.AuthorizedTickerServiceFactory

class TickerServiceGatewayUsingAuthorizedTickerService<T>(
    private val authorizedTickerServiceFactory: AuthorizedTickerServiceFactory<T>,
) : TickerServiceGateway<T> {

    override fun getTicker(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
    ): Ticker {
        return authorizedTickerServiceFactory.createAuthorizedTickerService(
            exchangeName = exchangeName,
            apiKey = apiKey,
        ).getTicker(currencyPair = currencyPair)
    }

    override fun getTickers(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPairs: Collection<CurrencyPair>,
    ): List<Ticker> {
        return authorizedTickerServiceFactory.createAuthorizedTickerService(
            exchangeName = exchangeName,
            apiKey = apiKey,
        ).getTickers(currencyPairs = currencyPairs)
    }

}
