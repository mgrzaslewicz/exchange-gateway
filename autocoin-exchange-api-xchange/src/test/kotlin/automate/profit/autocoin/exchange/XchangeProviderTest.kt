package automate.profit.autocoin.exchange

import automate.profit.autocoin.exchange.SupportedExchange.BITTREX
import automate.profit.autocoin.exchange.metadata.ExchangeMetadataService
import automate.profit.autocoin.exchange.peruser.ExchangeSpecificationVerifier
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.knowm.xchange.ExchangeFactory

class XchangeProviderTest {
    private lateinit var cachingXchangeProvider: CachingXchangeProvider
    private lateinit var xchangeFactoryWrapper: XchangeFactoryWrapper
    private lateinit var exchangeFactory: ExchangeFactory
    private lateinit var exchangeMetadataService: ExchangeMetadataService

    @BeforeEach
    fun setup() {
        xchangeFactoryWrapper = mock()
        exchangeFactory = mock()
        exchangeMetadataService = mock()
        cachingXchangeProvider = CachingXchangeProvider(
            xchangeSpecificationApiKeyAssigner = XchangeSpecificationApiKeyAssigner(ExchangeSpecificationVerifier()),
            xchangeFactoryWrapper = xchangeFactoryWrapper
        )
    }

    @Test
    fun shouldUseCachedXchangeFactoryWhenUsingKeys() {
        // given
        whenever(xchangeFactoryWrapper.createExchange(any())).thenReturn(mock())
        // when
        cachingXchangeProvider.getXchange(BITTREX, "public-key", "secret-key", null, emptyMap())
        cachingXchangeProvider.getXchange(BITTREX, "public-key", "secret-key", null, emptyMap())
        // then
        verify(xchangeFactoryWrapper, times(1)).createExchange(any())
    }

}
