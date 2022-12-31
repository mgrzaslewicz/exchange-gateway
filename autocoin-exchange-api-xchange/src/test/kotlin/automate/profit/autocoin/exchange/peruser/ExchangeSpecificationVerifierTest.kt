package automate.profit.autocoin.exchange.peruser

import automate.profit.autocoin.exchange.SupportedExchange.*
import automate.profit.autocoin.exchange.peruser.UserExchangeServicesFactory
import automate.profit.autocoin.exchange.peruser.XchangeFactory
import automate.profit.autocoin.exchange.peruser.XchangeMetadataFile
import automate.profit.autocoin.exchange.peruser.XchangeUserExchangeServicesFactory
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.knowm.xchange.ExchangeSpecification
import java.lang.IllegalArgumentException
import kotlin.test.assertFailsWith

class ExchangeSpecificationVerifierTest {

    private var exchangeSpecificationVerifier = ExchangeSpecificationVerifier()

    @Test(expected = IllegalArgumentException::class)
    fun shouldRejectTheSameApiAndSecretKey() {
        // given
        val exchangeSpecification = ExchangeSpecification("some exchange").apply {
            apiKey = "some-api-key"
            secretKey = "some-api-key"
        }
        // when
        exchangeSpecificationVerifier.verifyKeys(BITTREX, exchangeSpecification)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldRejectBitstampWithoutUsername() {
        // given
        val exchangeSpecification = ExchangeSpecification("some exchange").apply {
            apiKey = "some-api-key"
            secretKey = "some-api-secret"
        }
        // when
        exchangeSpecificationVerifier.verifyKeys(BITSTAMP, exchangeSpecification)
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

    @Test(expected = IllegalArgumentException::class)
    fun shouldRejectKucoinWithoutPassphrase() {
        // given
        val exchangeSpecification = ExchangeSpecification("some exchange").apply {
            apiKey = "some-api-key"
            secretKey = "some-api-secret"
        }
        // when
        exchangeSpecificationVerifier.verifyKeys(KUCOIN, exchangeSpecification)
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
