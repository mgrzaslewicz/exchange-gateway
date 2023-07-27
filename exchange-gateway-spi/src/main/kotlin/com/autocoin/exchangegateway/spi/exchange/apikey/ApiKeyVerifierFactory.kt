package com.autocoin.exchangegateway.spi.exchange.apikey


interface ApiKeyVerifierFactory {
    fun createApiKeyVerifier(exchangeName: String): ApiKeyVerifier
}
