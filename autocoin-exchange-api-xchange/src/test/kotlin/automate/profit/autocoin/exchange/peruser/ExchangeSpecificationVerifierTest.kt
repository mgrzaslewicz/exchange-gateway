package automate.profit.autocoin.exchange.peruser

import automate.profit.autocoin.exchange.SupportedExchange.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.knowm.xchange.ExchangeSpecification

class ExchangeSpecificationVerifierTest {

    private var exchangeSpecificationVerifier = ExchangeSpecificationVerifier()

    @Test
    fun shouldRejectTheSameApiAndSecretKey() {
        // given
        val exchangeSpecification = ExchangeSpecification("some exchange").apply {
            apiKey = "some-api-key"
            secretKey = "some-api-key"
        }
        // when
        assertThrows<IllegalArgumentException> {
            exchangeSpecificationVerifier.verifyKeys(BITTREX, exchangeSpecification)
        }

    }

    @Test
    fun shouldRejectBitstampWithoutUsername() {
        // given
        val exchangeSpecification = ExchangeSpecification("some exchange").apply {
            apiKey = "some-api-key"
            secretKey = "some-api-secret"
        }
        // when
        assertThrows<IllegalArgumentException> {
            exchangeSpecificationVerifier.verifyKeys(BITSTAMP, exchangeSpecification)
        }
    }

    @Test
    fun shouldNotRejectBitstampWithUsername() {
        // given
        val exchangeSpecification = ExchangeSpecification("some exchange").apply {
            apiKey = "some-api-key"
            secretKey = "some-api-secret"
            userName = "bitstamp-username"
        }
        // when
        exchangeSpecificationVerifier.verifyKeys(BITSTAMP, exchangeSpecification)
        // then no exception thrown
    }

    @Test
    fun shouldRejectKucoinWithoutPassphrase() {
        // given
        val exchangeSpecification = ExchangeSpecification("some exchange").apply {
            apiKey = "some-api-key"
            secretKey = "some-api-secret"
        }
        // when
        assertThrows<IllegalArgumentException> {
            exchangeSpecificationVerifier.verifyKeys(KUCOIN, exchangeSpecification)
        }
    }

    @Test
    fun shouldNotRejectKucoinWithPassphrase() {
        // given
        val exchangeSpecification = ExchangeSpecification("some exchange").apply {
            apiKey = "some-api-key"
            secretKey = "some-api-secret"
            exchangeSpecificParameters = mapOf("passphrase" to "some-kucoin-passphrase")
        }
        // when
        exchangeSpecificationVerifier.verifyKeys(KUCOIN, exchangeSpecification)
    }
}
