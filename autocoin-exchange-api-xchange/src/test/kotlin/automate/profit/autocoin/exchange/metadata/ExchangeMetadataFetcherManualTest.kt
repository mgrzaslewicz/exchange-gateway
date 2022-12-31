package automate.profit.autocoin.exchange.metadata

import org.junit.Ignore
import org.junit.Test

class ExchangeMetadataFetcherManualTest {

    @Test
    @Ignore
    fun shouldFetchBittrexMetadata() {
        val fetcher = BittrexExchangeMetadataFetcher()
        fetcher.fetchExchangeMetadata()
    }

    @Test
    @Ignore
    fun shouldFetchBinanceMetadata() {
        val fetcher = BinanceExchangeMetadataFetcher()
        fetcher.fetchExchangeMetadata()
    }

    @Test
    @Ignore
    fun shouldFetchKucoinMetadata() {
        val fetcher = KucoinExchangeMetadataFetcher()
        fetcher.fetchExchangeMetadata()
    }
}
