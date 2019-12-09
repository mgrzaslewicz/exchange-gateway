package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class ExchangeMetadataFetcherManualTest {

    @Test
    @Disabled
    fun shouldFetchBittrexMetadata() {
        val fetcher = BittrexExchangeMetadataFetcher()
        fetcher.fetchExchangeMetadata()
    }

    @Test
    @Disabled
    fun shouldFetchBinanceMetadata() {
        val fetcher = BinanceExchangeMetadataFetcher()
        fetcher.fetchExchangeMetadata()
    }

    @Test
    @Disabled
    fun shouldFetchGateioMetadata() {
        val fetcher = DefaultExchangeMetadataFetcher(SupportedExchange.GATEIO)
        fetcher.fetchExchangeMetadata()
    }

    @Test
    @Disabled
    fun shouldFetchKucoinMetadata() {
        val fetcher = KucoinExchangeMetadataFetcher()
        fetcher.fetchExchangeMetadata()
    }
}
