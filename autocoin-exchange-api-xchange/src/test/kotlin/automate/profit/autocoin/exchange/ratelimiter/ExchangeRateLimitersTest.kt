package automate.profit.autocoin.exchange.ratelimiter

import automate.profit.autocoin.exchange.SupportedExchange
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import java.time.Duration

class ExchangeRateLimitersTest {
    @Test
    fun shouldAllSupportedExchangesHaveRateLimiter() {
        // given
        val tested = ExchangeRateLimiters(permitAcquireTimeout = Duration.ZERO)
        val assertions = SoftAssertions()
        SupportedExchange.values().forEach {
            try {
                tested.get(it)
            } catch (e: Exception) {
                assertions.fail("Exchange $it has no rate limits defined")
            }
        }
        assertions.assertAll()
    }
}
