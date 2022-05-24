package automate.profit.autocoin.exchange.ratelimiter

import com.google.common.util.concurrent.RateLimiter
import java.time.Duration


class GuavaExchangeRateLimiter(
    permitsPerDuration: Long,
    duration: Duration,
    private val permitAcquireTimeout: Duration,
) : ExchangeRateLimiter {

    private val permitsPerSecond = permitsPerDuration / duration.seconds.toDouble()
    private val guavaRateLimiter = RateLimiter.create(permitsPerSecond)

    override fun acquirePermit() {
        guavaRateLimiter.acquire()
    }

    override fun tryAcquirePermit() = guavaRateLimiter.tryAcquire(permitAcquireTimeout)

}
