package automate.profit.autocoin.exchange.ratelimiter

class NoOpExchangeRateLimiter : ExchangeRateLimiter {
    override fun acquirePermit() {}

    override fun tryAcquirePermit() = true
}
