package automate.profit.autocoin.exchange.ratelimiter

import com.google.common.util.concurrent.RateLimiter
import java.time.Duration
import java.util.concurrent.TimeUnit

interface ExchangeRateLimiter {
    fun acquirePermit()
    fun tryAcquirePermit(timeout: Long, unit: TimeUnit): Boolean
}

class GuavaExchangeRateLimiter(private val permitsPerDuration: Long, private val duration: Duration) : ExchangeRateLimiter {

    private val permitsPerSecond = permitsPerDuration / duration.seconds.toDouble()
    private val guavaRateLimiter = RateLimiter.create(permitsPerSecond)

    override fun acquirePermit() {
        guavaRateLimiter.acquire()
    }

    override fun tryAcquirePermit(timeout: Long, unit: TimeUnit) = guavaRateLimiter.tryAcquire(timeout, unit)

}
