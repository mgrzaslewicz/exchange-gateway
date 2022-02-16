package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.metadata.binance.BinanceExchangeMetadataFetcher
import automate.profit.autocoin.exchange.metadata.bittrex.BittrexExchangeMetadataFetcher
import automate.profit.autocoin.exchange.metadata.kucoin.KucoinExchangeMetadataFetcher
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
        val fetcher = DefaultExchangeMetadataFetcher.Builder(
            supportedExchange = SupportedExchange.GATEIO,
            exchangeFactory = exchangeFactory,
        ).build()
        fetcher.fetchExchangeMetadata()
    }

    @Test
    fun shouldFetchKucoinMetadata() {
        val fetcher = KucoinExchangeMetadataFetcher(exchangeFactory)
        fetcher.fetchExchangeMetadata()
    }

    @Test
    fun shouldFetchHitBtcMetadata() {
        val fetcher = DefaultExchangeMetadataFetcher.Builder(
            supportedExchange = SupportedExchange.HITBTC,
            exchangeFactory = exchangeFactory,
        ).build()
        val metadata = fetcher.fetchExchangeMetadata()
        metadata.currencyMetadata
    }

    @Test
    fun shouldFetchKrakenMetadata() {
        val fetcher = DefaultExchangeMetadataFetcher.Builder(
            supportedExchange = SupportedExchange.KRAKEN,
            exchangeFactory = exchangeFactory,
            overridenCurrencies = krakenOverridenCurrenciesMetadata
        ).build()
        val metadata = fetcher.fetchExchangeMetadata()
        metadata.currencyMetadata
    }
}
