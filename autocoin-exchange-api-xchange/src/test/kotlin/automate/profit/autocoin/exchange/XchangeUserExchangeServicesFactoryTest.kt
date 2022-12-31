package automate.profit.autocoin.exchange

import automate.profit.autocoin.exchange.SupportedExchange.BITTREX
import automate.profit.autocoin.exchange.metadata.ExchangeMetadataProvider
import automate.profit.autocoin.exchange.metadata.FileExchangeMetadataRepository
import automate.profit.autocoin.exchange.metadata.exchangeMetadataFetchers
import automate.profit.autocoin.exchange.peruser.ExchangeSpecificationVerifier
import automate.profit.autocoin.exchange.peruser.UserExchangeServicesFactory
import automate.profit.autocoin.exchange.peruser.XchangeFactory
import automate.profit.autocoin.exchange.peruser.XchangeUserExchangeServicesFactory
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

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
                ExchangeMetadataProvider(
                        exchangeMetadataFetchers = exchangeMetadataFetchers,
                        exchangeMetadataRepository = FileExchangeMetadataRepository(tempFolder),
                        serviceApiKeysProvider = mock()
                ),
                ExchangeSpecificationVerifier(),
                serviceApiKeysProvider = mock()
        )
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
