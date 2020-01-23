package automate.profit.autocoin.exchange.apikey

import automate.profit.autocoin.exchange.SupportedExchange

/**
 * Provides API keys where these are not provided by user and needed
 * for reading public data like ticker, order book, metadata
 */
interface ServiceApiKeysProvider {
    fun getApiKeys(supportedExchange: SupportedExchange): ExchangeApiKey?
}