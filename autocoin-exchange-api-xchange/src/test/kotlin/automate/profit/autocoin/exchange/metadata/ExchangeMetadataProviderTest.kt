package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange.BITTREX
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ExchangeMetadataProviderTest {
    private val tempFolder = TemporaryFolder()

    @Before
    fun setup() {
        tempFolder.create()
    }

    @After
    fun cleanup() {
        tempFolder.delete()
    }

    @Test
    fun shouldFetchMetadataWhenRepositoryEmpty() {
        val expectedExchangeMetadata = ExchangeMetadata(emptyMap(), emptyMap())
        val expectedXchangeMetadata = XchangeMetadataJson("{}")
        val bittrexMetadataFetcher = mock<BittrexExchangeMetadataFetcher>().apply {
            whenever(this.supportedExchange).thenReturn(BITTREX)
            whenever(this.fetchExchangeMetadata()).thenReturn(Pair(expectedXchangeMetadata, expectedExchangeMetadata))
        }

        val metadataRepository = FileExchangeMetadataRepository(tempFolder.root)
        val metadataProvider = ExchangeMetadataProvider(listOf(bittrexMetadataFetcher), metadataRepository)
        // when
        val exchangeMetadata = metadataProvider.getAndSaveExchangeMetadata(BITTREX)
        // then
        assertThat(exchangeMetadata).isEqualTo(expectedExchangeMetadata)
    }

    @Test
    fun shouldNotFetchMetadataWhenRepositoryAlreadyHasMetadata() {
        val expectedExchangeMetadata = ExchangeMetadata(emptyMap(), emptyMap())
        val bittrexMetadataFetcher = mock<BittrexExchangeMetadataFetcher>().apply {
            whenever(this.supportedExchange).thenReturn(BITTREX)
        }
        val metadataRepository = mock<FileExchangeMetadataRepository>().apply {
            whenever(this.getLatestExchangeMetadata(BITTREX)).thenReturn(expectedExchangeMetadata)
        }
        val metadataProvider = ExchangeMetadataProvider(listOf(bittrexMetadataFetcher), metadataRepository)
        // when
        metadataProvider.getAndSaveExchangeMetadata(BITTREX)
        // then
        verify(bittrexMetadataFetcher, times(0)).fetchExchangeMetadata()
    }

}
