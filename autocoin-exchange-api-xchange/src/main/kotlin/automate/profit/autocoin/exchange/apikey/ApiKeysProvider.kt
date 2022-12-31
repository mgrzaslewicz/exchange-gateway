package automate.profit.autocoin.exchange.apikey

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import java.util.function.Supplier

/**
 * Provides API keys where these are not provided by user and needed
 * for reading public data like ticker, order book, metadata
 */
interface ApiKeysProvider {
    fun getApiKey(exchangeName: ExchangeName): Supplier<ApiKey>?
}
