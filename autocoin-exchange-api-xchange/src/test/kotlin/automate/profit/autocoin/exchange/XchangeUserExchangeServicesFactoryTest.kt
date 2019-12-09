package automate.profit.autocoin.exchange

import automate.profit.autocoin.exchange.SupportedExchange.BINANCE
import automate.profit.autocoin.exchange.SupportedExchange.BITTREX
import automate.profit.autocoin.exchange.metadata.ExchangeMetadataProvider
import automate.profit.autocoin.exchange.metadata.FileExchangeMetadataRepository
import automate.profit.autocoin.exchange.metadata.exchangeMetadataFetchers
import automate.profit.autocoin.exchange.peruser.ExchangeSpecificationVerifier
import automate.profit.autocoin.exchange.peruser.UserExchangeServicesFactory
import automate.profit.autocoin.exchange.peruser.XchangeFactory
import automate.profit.autocoin.exchange.peruser.XchangeUserExchangeServicesFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertFailsWith

class XchangeUserExchangeServicesFactoryTest {
    @TempDir
    lateinit var tempFolder: File
    private lateinit var userExchangeServicesFactory: UserExchangeServicesFactory
    private lateinit var xchangeFactory: XchangeFactory

    @BeforeEach
    fun setup() {
        xchangeFactory = spy(XchangeFactory())
        userExchangeServicesFactory = XchangeUserExchangeServicesFactory(
                xchangeFactory,
                ExchangeMetadataProvider(exchangeMetadataFetchers, FileExchangeMetadataRepository(tempFolder)),
                ExchangeSpecificationVerifier()
        )
    }

    @Test
    fun shouldCreateTickerListenerRegistrarFromValidName() {
        // when
        val autocoinExchange = userExchangeServicesFactory.createTickerListenerRegistrar(BITTREX.exchangeName)
        // then
        assertThat(autocoinExchange).isNotNull
    }

    @Test
    fun shouldThrowWhenCreateTickerListenerRegistrarFromInvalidName() {
        // given
        val invalidExchangeName = "invalidName"
        assertFailsWith(RuntimeException::class) {
            // when
            userExchangeServicesFactory.createTickerListenerRegistrar(invalidExchangeName)
        }
    }

    @Test
    fun shouldReturnCachedAutocoinExchangeWhenUsingNoApiKeys() {
        // when
        userExchangeServicesFactory.createTickerListenerRegistrar(BITTREX.exchangeName)
        userExchangeServicesFactory.createTickerListenerRegistrar(BITTREX.exchangeName)
        // then
        verify(xchangeFactory, times(1)).createExchange(any())
    }

    @Test
    fun shouldNotUseCachedXchangeFactoryWhenUsingNoApiKeysAnd2DifferentExchanges() {
        // when
        userExchangeServicesFactory.createTickerListenerRegistrar(BITTREX.exchangeName)
        userExchangeServicesFactory.createTickerListenerRegistrar(BINANCE.exchangeName)
        // then
        verify(xchangeFactory, times(2)).createExchange(any())
    }

    @Test
    fun shouldUseCachedXchangeFactoryWhenUsingKeys() {
        // when
        userExchangeServicesFactory.createTradeService(BITTREX.exchangeName, "public-key", "secret-key", null, emptyMap())
        userExchangeServicesFactory.createTradeService(BITTREX.exchangeName, "public-key", "secret-key", null, emptyMap())
        // then
        verify(xchangeFactory, times(1)).createExchange(any())
    }

}
