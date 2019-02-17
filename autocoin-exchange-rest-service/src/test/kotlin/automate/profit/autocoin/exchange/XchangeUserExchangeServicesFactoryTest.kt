package automate.profit.autocoin.exchange

import automate.profit.autocoin.exchange.SupportedExchange.BINANCE
import automate.profit.autocoin.exchange.SupportedExchange.BITTREX
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
import kotlin.test.assertFailsWith

class XchangeUserExchangeServicesFactoryTest {

    private lateinit var userExchangeServicesFactory: UserExchangeServicesFactory
    private lateinit var xchangeFactory: XchangeFactory

    @Before
    fun setup() {
        xchangeFactory = spy(XchangeFactory())
        userExchangeServicesFactory = XchangeUserExchangeServicesFactory(xchangeFactory, XchangeMetadataFile())
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
    fun shouldReturnMetadataProvider() {
        // when
        userExchangeServicesFactory.createMetadataProvider(BITTREX.exchangeName)
        // then
        verify(xchangeFactory, times(1)).createExchange(any())
    }

    @Test
    fun shouldUseCachedXchangeFactoryWhenUsingKeys() {
        // when
        userExchangeServicesFactory.createTradeService(BITTREX.exchangeName, "public-key", "secret-key", null)
        userExchangeServicesFactory.createTradeService(BITTREX.exchangeName, "public-key", "secret-key", null)
        // then
        verify(xchangeFactory, times(1)).createExchange(any())
    }

}
