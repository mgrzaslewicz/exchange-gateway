package automate.profit.autocoin.exchange.ratelimiter

import automate.profit.autocoin.exchange.SupportedExchange
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.MINUTES
import java.time.temporal.ChronoUnit.SECONDS

class ExchangeRateLimiters(
    private val defaultPermitsPerDuration: Long = 500L,
    private val defaultDuration: Duration = Duration.of(1L, MINUTES),
    private val timeout: Duration,
    private val createRateLimiterFunction: (permitsPerDuration: Long, duration: Duration) -> ExchangeRateLimiter = { permitsPerDuration, duration ->
        GuavaExchangeRateLimiter(
            permitsPerDuration = permitsPerDuration,
            duration = duration,
            timeout = timeout,
        )
    },

    ) {
    private data class RateLimit(
        val permitsPerDuration: Long,
        val duration: Duration,
    )

    private val nonDefaultRateLimiters = mapOf(
        // https://python-binance.readthedocs.io/en/latest/overview.html#:~:text=API%20Rate%20Limit,-Check%20the%20get_exchange_info&text=At%20the%20current%20time%20Binance,100%2C000%20orders%20per%2024hrs
        SupportedExchange.BINANCE to RateLimit(1200L, Duration.of(1L, MINUTES)),

        // https://docs.bitfinex.com/docs/requirements-and-limitations#rest-rate-limits
        SupportedExchange.BITFINEX to RateLimit(90L, Duration.of(1L, MINUTES)),

        // https://www.bitmex.com/app/restAPI
        // 120 requests per minute on all routes (reduced to 30 when unauthenticated)
        SupportedExchange.BITMEX to RateLimit(30L, Duration.of(1L, MINUTES)),


        // https://bitsoex.github.io/slate/#developer-testing-server
        SupportedExchange.BITSO to RateLimit(60L, Duration.of(1L, MINUTES)),

        // https://www.bitstamp.net/api/#what-is-api Do not make more than 8000 requests per 10 minutes or we will ban your IP address
        SupportedExchange.BITSTAMP to RateLimit(8000L, Duration.of(10L, MINUTES)),

        // https://bittrex.github.io/api/v3#topic-Best-Practices
        SupportedExchange.BITTREX to RateLimit(60L, Duration.of(1L, MINUTES)),

        // https://api.hitbtc.com/#:~:text=RATE%20LIMITING,-The%20following%20Rate&text=for%20Trading%2C%20the%20limit%20is,per%20second%20for%20one%20user.
        SupportedExchange.HITBTC to RateLimit(30L, Duration.of(1L, SECONDS)),

        // https://www.reddit.com/r/cexio/comments/8og8up/api_rate_limits/
        SupportedExchange.CEXIO to RateLimit(600L, Duration.of(10L, MINUTES)),

        // https://help.coinbase.com/en/pro/other-topics/api/faq-on-api
        SupportedExchange.COINBASEPRO to RateLimit(3L, Duration.of(1L, SECONDS)),

        // https://coindeal.com/support/article/does-coindeal-have-api/
        SupportedExchange.COINDEAL to RateLimit(80L, Duration.of(1L, SECONDS)),

        // https://documenter.getpostman.com/view/10287440/SzYXWKPi#:~:text=API%20Rate%20Limits,or%20by%20a%20single%20user.
        SupportedExchange.EXMO to RateLimit(10L, Duration.of(1L, SECONDS)),

        // https://www.gate.io/docs/apiv4/en/#frequency-limit-rule
        SupportedExchange.GATEIO to RateLimit(300L, Duration.of(1L, SECONDS)),

        // https://support.kraken.com/hc/en-us/articles/206548367-What-are-the-API-rate-limits-
        SupportedExchange.KRAKEN to RateLimit(1L, Duration.of(1L, SECONDS)),

        // https://www.kucoin.com/news/en-kucoin-request-limit-for-api-upgrade#:~:text=For%20average%20users%2C%20the%20request,API%20key%20for%205%20minutes.
        SupportedExchange.KUCOIN to RateLimit(1800L, Duration.of(1L, MINUTES)),

        // https://futures-docs.poloniex.com/#introduction
        SupportedExchange.POLONIEX to RateLimit(180L, Duration.of(1L, MINUTES)),

    )

    private val rateLimiters = SupportedExchange
        .values()
        .associateWith {
            if (nonDefaultRateLimiters.containsKey(it)) {
                createRateLimiterFunction(nonDefaultRateLimiters[it]!!.permitsPerDuration, nonDefaultRateLimiters[it]!!.duration)
            } else {
                createRateLimiterFunction(defaultPermitsPerDuration, defaultDuration)
            }
        }

    fun get(supportedExchange: SupportedExchange) = rateLimiters[supportedExchange]!!
}
