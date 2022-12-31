package com.autocoin.exchangegateway.spi.exchange.ratelimiter

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKey
import java.util.function.Supplier


interface RateLimitedEndpoint

interface ExchangeRateLimiterGateway {
    fun acquirePermit(
        exchangeName: ExchangeName,
        rateLimitedEndpoint: RateLimitedEndpoint,
        apiKey: Supplier<ApiKey>?,
    )

    fun tryAcquirePermit(
        exchangeName: ExchangeName,
        rateLimitedEndpoint: RateLimitedEndpoint,
        apiKey: Supplier<ApiKey>?,
    ): Boolean
}

class RateLimiterTimeoutException(message: String) : RuntimeException(message)
