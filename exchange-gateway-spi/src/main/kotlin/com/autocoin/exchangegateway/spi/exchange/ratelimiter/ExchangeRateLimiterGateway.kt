package com.autocoin.exchangegateway.spi.exchange.ratelimiter

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier


interface RateLimitedEndpoint

interface ExchangeRateLimiterGateway<T> {
    fun acquirePermit(
        exchangeName: ExchangeName,
        rateLimitedEndpoint: RateLimitedEndpoint,
        apiKey: ApiKeySupplier<T>,
    )

    fun tryAcquirePermit(
        exchangeName: ExchangeName,
        rateLimitedEndpoint: RateLimitedEndpoint,
        apiKey: ApiKeySupplier<T>,
    ): Boolean
}

class RateLimiterTimeoutException(message: String) : RuntimeException(message)
