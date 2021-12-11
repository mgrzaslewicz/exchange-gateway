package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.knowm.xchange.ExchangeFactory

@Disabled
class ExchangeMetadataFetcherManualTest {

    private val exchangeFactory = ExchangeFactory.INSTANCE

    @Test
    fun shouldFetchBittrexMetadata() {
        val fetcher = BittrexExchangeMetadataFetcher(exchangeFactory)
        fetcher.fetchExchangeMetadata()
    }

    @Test
    fun shouldFetchBinanceMetadata() {
        val fetcher = BinanceExchangeMetadataFetcher(exchangeFactory)
        fetcher.fetchExchangeMetadata()
    }

    @Test
    fun shouldFetchGateioMetadata() {
        val fetcher = DefaultExchangeMetadataFetcher(SupportedExchange.GATEIO, exchangeFactory)
        fetcher.fetchExchangeMetadata()
    }

    @Test
    fun shouldFetchKucoinMetadata() {
        val fetcher = KucoinExchangeMetadataFetcher(exchangeFactory)
        fetcher.fetchExchangeMetadata()
    }
}
