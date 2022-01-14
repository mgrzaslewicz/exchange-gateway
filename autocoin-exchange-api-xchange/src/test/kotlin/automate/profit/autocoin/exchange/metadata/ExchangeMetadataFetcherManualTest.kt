package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.XchangeSpecificationApiKeyAssigner
import automate.profit.autocoin.exchange.metadata.binance.BinanceExchangeMetadataFetcher
import automate.profit.autocoin.exchange.metadata.bittrex.BittrexExchangeMetadataFetcher
import automate.profit.autocoin.exchange.metadata.kucoin.KucoinExchangeMetadataFetcher
import automate.profit.autocoin.exchange.peruser.ExchangeSpecificationVerifier
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.knowm.xchange.ExchangeFactory

@Disabled
class ExchangeMetadataFetcherManualTest {
    private val xchangeSpecificationApiKeyAssigner = XchangeSpecificationApiKeyAssigner(ExchangeSpecificationVerifier())

    private val exchangeFactory = ExchangeFactory.INSTANCE

    @Test
    fun shouldFetchBittrexMetadata() {
        val fetcher = BittrexExchangeMetadataFetcher(exchangeFactory, xchangeSpecificationApiKeyAssigner = xchangeSpecificationApiKeyAssigner)
        fetcher.fetchExchangeMetadata()
    }

    @Test
    fun shouldFetchBinanceMetadata() {
        val fetcher = BinanceExchangeMetadataFetcher(exchangeFactory, xchangeSpecificationApiKeyAssigner = xchangeSpecificationApiKeyAssigner)
        fetcher.fetchExchangeMetadata()
    }

    @Test
    fun shouldFetchGateioMetadata() {
        val fetcher = DefaultExchangeMetadataFetcher.Builder(
            supportedExchange = SupportedExchange.GATEIO,
            exchangeFactory = exchangeFactory,
            xchangeSpecificationApiKeyAssigner = xchangeSpecificationApiKeyAssigner
        ).build()
        fetcher.fetchExchangeMetadata()
    }

    @Test
    fun shouldFetchKucoinMetadata() {
        val fetcher = KucoinExchangeMetadataFetcher(exchangeFactory, xchangeSpecificationApiKeyAssigner = xchangeSpecificationApiKeyAssigner)
        fetcher.fetchExchangeMetadata()
    }
}
