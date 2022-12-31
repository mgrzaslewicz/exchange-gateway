package automate.profit.autocoin.spi.exchange.apikey

import automate.profit.autocoin.spi.exchange.ExchangeName

interface ApiKeyVerifierGateway {
    fun verifyApiKey(exchangeName: ExchangeName, apiKey: ApiKey)
}

interface ApiKeyVerifier {
    fun verifyApiKey(apiKey: ApiKey)
}
