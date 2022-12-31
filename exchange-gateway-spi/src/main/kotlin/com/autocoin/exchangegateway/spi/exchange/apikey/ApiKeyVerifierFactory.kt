package com.autocoin.exchangegateway.spi.exchange.apikey

import com.autocoin.exchangegateway.spi.exchange.ExchangeName

interface ApiKeyVerifierFactory {
    fun createApiKeyVerifier(exchangeName: ExchangeName): ApiKeyVerifier
}
