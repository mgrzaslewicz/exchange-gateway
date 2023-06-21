package com.autocoin.exchangegateway.spi.exchange.ratelimiter

interface RateLimiterProvider<T> {
    operator fun invoke(identifier: T): RateLimiter
}
