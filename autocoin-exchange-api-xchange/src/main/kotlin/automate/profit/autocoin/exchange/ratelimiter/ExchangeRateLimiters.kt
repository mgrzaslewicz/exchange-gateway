package automate.profit.autocoin.exchange.ratelimiter

import automate.profit.autocoin.exchange.SupportedExchange
import java.time.Duration
import java.time.temporal.ChronoUnit

class ExchangeRateLimiters(
    private val permitsPerDuration: Long = 500L,
    private val duration: Duration = Duration.of(1L, ChronoUnit.MINUTES),
    private val timeout: Duration,
    private val createRateLimiterFunction: (permitsPerDuration: Long, duration: Duration) -> ExchangeRateLimiter = { permitsPerDuration, duration ->
        GuavaExchangeRateLimiter(
            permitsPerDuration = permitsPerDuration,
            duration = duration,
            timeout = timeout,
        )
    },

    ) {
    private val rateLimiters = SupportedExchange.values().associateWith {
        createRateLimiterFunction(permitsPerDuration, duration)
    }

    fun get(supportedExchange: SupportedExchange) = rateLimiters[supportedExchange]!!
}
