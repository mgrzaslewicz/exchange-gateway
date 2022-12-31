package automate.profit.autocoin.exchange.ratelimiter

import java.util.concurrent.TimeUnit

class NoOpExchangeRateLimiter : ExchangeRateLimiter {
    override fun acquirePermit() {}

    override fun tryAcquirePermit(timeout: Long, unit: TimeUnit) = true
}
