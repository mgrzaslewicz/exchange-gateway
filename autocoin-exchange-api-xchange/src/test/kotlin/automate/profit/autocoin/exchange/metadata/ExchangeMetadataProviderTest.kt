package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange.BITTREX
import automate.profit.autocoin.exchange.metadata.bittrex.BittrexExchangeMetadataFetcher
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class ExchangeMetadataProviderTest {

    @Test
    fun shouldFetchMetadataWhenRepositoryEmpty(@TempDir tempFolder: File) {
        val expectedExchangeMetadata = ExchangeMetadata(emptyMap(), emptyMap())
        val expectedXchangeMetadata = XchangeMetadataJson("{}")
        val bittrexMetadataFetcher = mock<BittrexExchangeMetadataFetcher>().apply {
            whenever(this.supportedExchange).thenReturn(BITTREX)
            whenever(this.fetchExchangeMetadata()).thenReturn(Pair(expectedXchangeMetadata, expectedExchangeMetadata))
        }

        val metadataRepository = FileExchangeMetadataRepository(tempFolder)
        val metadataProvider = ExchangeMetadataProvider(listOf(bittrexMetadataFetcher), metadataRepository, mock())
        // when
        val exchangeMetadata = metadataProvider.getAndSaveExchangeMetadata(BITTREX)
        // then
        assertThat(exchangeMetadata).isEqualTo(expectedExchangeMetadata)
    }

    @Test
    fun shouldNotFetchMetadataWhenRepositoryAlreadyHasMetadata() {
        val expectedExchangeMetadataResult = ExchangeMetadataResult(exchangeMetadata = ExchangeMetadata(emptyMap(), emptyMap()))
        val bittrexMetadataFetcher = mock<BittrexExchangeMetadataFetcher>().apply {
            whenever(this.supportedExchange).thenReturn(BITTREX)
        }
        val metadataRepository = mock<FileExchangeMetadataRepository>().apply {
            whenever(this.getLatestExchangeMetadata(BITTREX)).thenReturn(expectedExchangeMetadataResult)
        }
        val metadataProvider = ExchangeMetadataProvider(listOf(bittrexMetadataFetcher), metadataRepository, mock())
        // when
        metadataProvider.getAndSaveExchangeMetadata(BITTREX)
        // then
        verify(bittrexMetadataFetcher, times(0)).fetchExchangeMetadata()
    }

}
