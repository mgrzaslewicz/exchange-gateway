package automate.profit.autocoin.spi.exchange.ratelimiter

enum class RateLimiterBehavior {
    NO_RATE_LIMIT,
    WAIT_WITHOUT_TIMEOUT,
    WAIT_WITH_TIMEOUT,
    WAIT_WITH_TIMEOUT_AND_THROW_EXCEPTION,
}
