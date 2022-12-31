package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.SupportedExchange.*
import automate.profit.autocoin.exchange.XchangeSpecificationApiKeyAssigner
import automate.profit.autocoin.exchange.apikey.ExchangeApiKey
import automate.profit.autocoin.exchange.peruser.ExchangeSpecificationVerifier
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.knowm.xchange.ExchangeFactory
import java.lang.System.getProperty


@Disabled
class ExchangeMetadataFetcherManualTest {
    private val exchangeMetadataFetchers =
        exchangeMetadataFetchers(ExchangeFactory.INSTANCE, XchangeSpecificationApiKeyAssigner(ExchangeSpecificationVerifier())).associateBy { it.supportedExchange }

    @Test
    fun shouldFetchBittrexMetadata() {
        val metadata = exchangeMetadataFetchers.getValue(BITTREX).fetchExchangeMetadata()
        assertsFor(metadata)
    }

    @Test
    fun shouldFetchBinanceMetadata() {
        val metadata = exchangeMetadataFetchers.getValue(BINANCE).fetchExchangeMetadata()
        assertsFor(metadata)
    }

    @Test
    fun shouldFetchGateioMetadata() {
        val metadata = exchangeMetadataFetchers.getValue(GATEIO).fetchExchangeMetadata()
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
        val metadata = exchangeMetadataFetchers.getValue(KUCOIN).fetchExchangeMetadata()
        assertsFor(metadata)
    }

    @Test
    fun shouldFetchHitBtcMetadata() {
        val metadata = exchangeMetadataFetchers.getValue(HITBTC).fetchExchangeMetadata()
        assertsFor(metadata)
    }

    @Test
    fun shouldFetchCexioBtcMetadata() {
        val metadata = exchangeMetadataFetchers.getValue(CEXIO).fetchExchangeMetadata()
        assertsFor(metadata)
    }

    @Test
    fun shouldFetchKrakenMetadata() {
        val fetcher = exchangeMetadataFetchers.getValue(KRAKEN)
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
        val exchange = BITFINEX
        val metadata = exchangeMetadataFetchers.getValue(BITFINEX).fetchExchangeMetadata(exchangeApiKeyFromProperties(exchange))
        assertsFor(metadata)
    }

    @Test
    fun shouldFetchPoloniexMetadata() {
        val exchange = POLONIEX
        val metadata = exchangeMetadataFetchers.getValue(exchange).fetchExchangeMetadata(exchangeApiKeyFromProperties(exchange))
        assertsFor(metadata)
    }
}
