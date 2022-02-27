package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.SupportedExchange.BITFINEX
import automate.profit.autocoin.exchange.apikey.ExchangeApiKey
import automate.profit.autocoin.exchange.metadata.binance.BinanceExchangeMetadataFetcher
import automate.profit.autocoin.exchange.metadata.bittrex.BittrexExchangeMetadataFetcher
import automate.profit.autocoin.exchange.metadata.kucoin.KucoinExchangeMetadataFetcher
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.knowm.xchange.ExchangeFactory
import java.lang.System.getProperty


@Disabled
class ExchangeMetadataFetcherManualTest {
    private val exchangeFactory = ExchangeFactory.INSTANCE

    @Test
    fun shouldFetchBittrexMetadata() {
        val fetcher = BittrexExchangeMetadataFetcher(exchangeFactory)
        val metadata = fetcher.fetchExchangeMetadata()
        assertsFor(metadata)
    }

    @Test
    fun shouldFetchBinanceMetadata() {
        val fetcher = BinanceExchangeMetadataFetcher(exchangeFactory)
        val metadata = fetcher.fetchExchangeMetadata()
        assertsFor(metadata)
    }

    @Test
    fun shouldFetchGateioMetadata() {
        val fetcher = DefaultExchangeMetadataFetcher.Builder(
            supportedExchange = SupportedExchange.GATEIO,
            exchangeFactory = exchangeFactory,
        ).build()
        val metadata = fetcher.fetchExchangeMetadata()
        assertsFor(metadata)
    }

    private fun assertsFor(metadata: ExchangeMetadata) {
        val currencyMetadata = metadata.currencyMetadata
        val currencyPairMetadata = metadata.currencyPairMetadata

        val percentOfCurrenciesHavingWithdrawalFees = currencyMetadata.count { it.value.withdrawalFeeAmount != null } * 100.0 / currencyMetadata.size
        val percentOfCurrenciesHavingDepositEnabled = currencyMetadata.count { it.value.depositEnabled ?: false } * 100.0 / currencyMetadata.size
        val percentOfCurrenciesHavingWithdrawalsEnabled = currencyMetadata.count { it.value.withdrawalEnabled ?: false } * 100.0 / currencyMetadata.size

        val percentOfCurrencyPairsHavingTradingFeeRanges = currencyPairMetadata.count { it.value.transactionFeeRanges.takerFees.isNotEmpty() } * 100.0 / currencyPairMetadata.size

        SoftAssertions().apply {
            assertThat(percentOfCurrenciesHavingWithdrawalFees).isGreaterThan(20.0)
            assertThat(percentOfCurrencyPairsHavingTradingFeeRanges).isGreaterThan(20.0)
            assertThat(percentOfCurrenciesHavingDepositEnabled).isGreaterThan(20.0)
            assertThat(percentOfCurrenciesHavingWithdrawalsEnabled).isGreaterThan(20.0)
        }.assertAll()
    }

    @Test
    fun shouldFetchKucoinMetadata() {
        val fetcher = KucoinExchangeMetadataFetcher(exchangeFactory)
        val metadata = fetcher.fetchExchangeMetadata()
        assertsFor(metadata)
    }

    @Test
    fun shouldFetchHitBtcMetadata() {
        val fetcher = DefaultExchangeMetadataFetcher.Builder(
            supportedExchange = SupportedExchange.HITBTC,
            exchangeFactory = exchangeFactory,
        ).build()
        val metadata = fetcher.fetchExchangeMetadata()
        assertsFor(metadata)
    }

    @Test
    fun shouldFetchKrakenMetadata() {
        val fetcher = DefaultExchangeMetadataFetcher.Builder(
            supportedExchange = SupportedExchange.KRAKEN,
            exchangeFactory = exchangeFactory,
            overridenCurrencies = krakenOverridenCurrenciesMetadata
        ).build()
        val metadata = fetcher.fetchExchangeMetadata()
        assertsFor(metadata)
    }

    private fun exchangeApiKeyFromProperties(supportedExchange: SupportedExchange): ExchangeApiKey? {
        val exchangeName = supportedExchange.exchangeName
        val publicKeyPropertyName = "$exchangeName-publicKey"
        val secretKeyPropertyName = "$exchangeName-secretKey"
        val publicKey: String? = getProperty(publicKeyPropertyName)
        val secretKey: String? = getProperty(secretKeyPropertyName)
        return if (publicKey != null && secretKey != null) {
            ExchangeApiKey(
                publicKey = publicKey,
                secretKey = secretKey
            )
        } else {
            null
        }
    }

    @Test
    fun shouldFetchBitfinexMetadata() {
        val fetcher = DefaultExchangeMetadataFetcher.Builder(
            supportedExchange = BITFINEX,
            exchangeFactory = exchangeFactory,
        ).build()
        val metadata = fetcher.fetchExchangeMetadata(exchangeApiKeyFromProperties(BITFINEX))
        assertsFor(metadata)
    }
}
