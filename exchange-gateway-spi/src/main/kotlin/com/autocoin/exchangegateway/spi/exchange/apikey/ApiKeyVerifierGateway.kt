package com.autocoin.exchangegateway.spi.exchange.apikey

import com.autocoin.exchangegateway.spi.exchange.ExchangeName

interface ApiKeyVerifierGateway {
    fun verifyApiKey(
        exchangeName: ExchangeName,
        apiKey: ApiKey,
    )
}

interface ApiKeyVerifier {
    fun verifyApiKey(apiKey: ApiKey)
}
