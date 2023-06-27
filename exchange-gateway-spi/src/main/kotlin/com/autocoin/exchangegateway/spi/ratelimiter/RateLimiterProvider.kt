package com.autocoin.exchangegateway.spi.ratelimiter

interface RateLimiterProvider<T> {
    operator fun invoke(identifier: T): RateLimiter
}
