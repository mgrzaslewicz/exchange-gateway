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

class DefaultExchangeMetadataServiceTest {

    private val emptyMetadata = ExchangeMetadata(
        exchange = BITTREX,
        currencyPairMetadata = emptyMap(),
        currencyMetadata = emptyMap(),
        debugWarnings = emptyList()
    )

    @Test
    fun shouldFetchMetadataWhenRepositoryEmpty(@TempDir tempFolder: File) {
        val expectedExchangeMetadata = emptyMetadata
        val bittrexMetadataFetcher = mock<BittrexExchangeMetadataFetcher>().apply {
            whenever(this.supportedExchange).thenReturn(BITTREX)
            whenever(this.fetchExchangeMetadata()).thenReturn(expectedExchangeMetadata)
        }

        val metadataRepository = FileExchangeMetadataRepository(tempFolder)
        val metadataService = DefaultExchangeMetadataService(listOf(bittrexMetadataFetcher), metadataRepository, mock())
        // when
        val exchangeMetadata = metadataService.getAndSaveExchangeMetadata(BITTREX)
        // then
        assertThat(exchangeMetadata).isEqualTo(expectedExchangeMetadata)
    }

    @Test
    fun shouldNotFetchMetadataWhenRepositoryAlreadyHasMetadata() {
        val expectedExchangeMetadataResult = ExchangeMetadataResult(exchangeMetadata = emptyMetadata)
        val bittrexMetadataFetcher = mock<BittrexExchangeMetadataFetcher>().apply {
            whenever(this.supportedExchange).thenReturn(BITTREX)
        }
        val metadataRepository = mock<FileExchangeMetadataRepository>().apply {
            whenever(this.getLatestExchangeMetadata(BITTREX)).thenReturn(expectedExchangeMetadataResult)
        }
        val metadataService = DefaultExchangeMetadataService(listOf(bittrexMetadataFetcher), metadataRepository, mock())
        // when
        metadataService.getAndSaveExchangeMetadata(BITTREX)
        // then
        verify(bittrexMetadataFetcher, times(0)).fetchExchangeMetadata()
    }

}
