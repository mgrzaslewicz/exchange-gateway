package automate.profit.autocoin.exchange

import automate.profit.autocoin.exchange.SupportedExchange.BITTREX
import automate.profit.autocoin.exchange.metadata.ExchangeMetadataProvider
import automate.profit.autocoin.exchange.peruser.ExchangeSpecificationVerifier
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.knowm.xchange.ExchangeFactory
import java.io.File

class XchangeProviderTest {
    private lateinit var cachingXchangeProvider: CachingXchangeProvider
    private lateinit var xchangeFactoryWrapper: XchangeFactoryWrapper
    private lateinit var exchangeFactory: ExchangeFactory
    private lateinit var exchangeMetadataProvider: ExchangeMetadataProvider

    @BeforeEach
    fun setup() {
        xchangeFactoryWrapper = mock()
        exchangeFactory = mock()
        exchangeMetadataProvider = mock()
        cachingXchangeProvider = CachingXchangeProvider(
            xchangeSpecificationApiKeyAssigner = XchangeSpecificationApiKeyAssigner(ExchangeSpecificationVerifier()),
            xchangeFactoryWrapper = xchangeFactoryWrapper,
            exchangeMetadataProvider = exchangeMetadataProvider
        )
    }

    @Test
    fun shouldUseCachedXchangeFactoryWhenUsingKeys() {
        // given
        whenever(xchangeFactoryWrapper.createExchange(any())).thenReturn(mock())
        whenever(exchangeMetadataProvider.getAndSaveXchangeMetadataFile(BITTREX)).thenReturn(File("/does-not-matter"))
        // when
        cachingXchangeProvider.getXchange(BITTREX, "public-key", "secret-key", null, emptyMap())
        cachingXchangeProvider.getXchange(BITTREX, "public-key", "secret-key", null, emptyMap())
        // then
        verify(xchangeFactoryWrapper, times(1)).createExchange(any())
    }

}
