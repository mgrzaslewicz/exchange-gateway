package com.autocoin.exchangegateway.spi.exchange.ratelimiter

interface RateLimiter {
    /**
     * @return seconds waited
     */
    fun acquire(): Double
    fun tryAcquire(): Boolean
}

class RateLimiterTimeoutException(message: String) : RuntimeException(message)

fun RateLimiter.acquireWith(
    behavior: RateLimiterBehavior,
    messageWhenFailedToAcquire: () -> String,
) {
    when (behavior) {
        RateLimiterBehavior.WAIT_WITH_TIMEOUT -> tryAcquire()
        RateLimiterBehavior.WAIT_WITH_TIMEOUT_AND_THROW_EXCEPTION -> {
            if (!tryAcquire()) {
                throw RateLimiterTimeoutException(messageWhenFailedToAcquire())
            }
        }

        RateLimiterBehavior.WAIT_WITHOUT_TIMEOUT -> acquire()
        RateLimiterBehavior.NO_RATE_LIMIT -> return
    }
}
