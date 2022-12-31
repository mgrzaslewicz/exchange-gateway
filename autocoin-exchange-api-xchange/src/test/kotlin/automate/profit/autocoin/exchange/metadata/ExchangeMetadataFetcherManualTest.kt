package automate.profit.autocoin.exchange.metadata

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ExchangeMetadataFetcherManualTest {
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
    fun shouldFetchBittrexMetadata() {
        val fetcher = BittrexExchangeMetadataFetcher()
        fetcher.fetchExchangeMetadata()
    }

    @Test
    fun shouldFetchBinanceMetadata() {
        val fetcher = BinanceExchangeMetadataFetcher()
        fetcher.fetchExchangeMetadata()
    }

    @Test
    fun shouldFetchKucoinMetadata() {
        val fetcher = KucoinExchangeMetadataFetcher()
        fetcher.fetchExchangeMetadata()
    }
}
