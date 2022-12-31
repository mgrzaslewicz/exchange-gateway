package automate.profit.autocoin.exchange.ratelimiter

interface ExchangeRateLimiter {
    fun acquirePermit()
    fun tryAcquirePermit(): Boolean
}

class RateLimiterTimeoutException(message: String): RuntimeException(message)

fun ExchangeRateLimiter.acquireWith(behavior: RateLimiterBehavior, messageWhenFailedToAcquire: () -> String) {
    when (behavior) {
        RateLimiterBehavior.WAIT_WITH_TIMEOUT -> tryAcquirePermit()
        RateLimiterBehavior.WAIT_WITH_TIMEOUT_AND_THROW_EXCEPTION -> {
            if (!tryAcquirePermit()) {
               throw RateLimiterTimeoutException(messageWhenFailedToAcquire())
            }
        }
        RateLimiterBehavior.WAIT_WITHOUT_TIMEOUT -> acquirePermit()
        RateLimiterBehavior.NO_RATE_LIMIT -> return
    }
}
