package com.autocoin.exchangegateway.spi.exchange.apikey

import com.autocoin.exchangegateway.spi.exchange.Exchange

interface ApiKeyVerifierGateway {
    fun verifyApiKey(
        exchange: Exchange,
        apiKey: ApiKey,
    )
}

interface ApiKeyVerifier {
    fun verifyApiKey(apiKey: ApiKey)
}
