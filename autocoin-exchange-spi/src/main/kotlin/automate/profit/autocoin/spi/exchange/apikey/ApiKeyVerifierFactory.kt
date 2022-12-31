package automate.profit.autocoin.spi.exchange.apikey

import automate.profit.autocoin.spi.exchange.ExchangeName

interface ApiKeyVerifierFactory {
    fun createApiKeyVerifier(exchangeName: ExchangeName): ApiKeyVerifier
}
