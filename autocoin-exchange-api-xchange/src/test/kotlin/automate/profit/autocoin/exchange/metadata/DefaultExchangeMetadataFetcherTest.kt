package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.apikey.ExchangeApiKey
import automate.profit.autocoin.exchange.currency.CurrencyPair
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.dto.meta.ExchangeMetaData
import java.math.BigDecimal

class DefaultExchangeMetadataFetcherTest {
    private val sampleMetadataJson = this::class.java.getResource("/metadata/sample-metadata.json").readText()
    private val sampleMetadata: ExchangeMetaData = metadataObjectMapper.readValue(sampleMetadataJson, ExchangeMetaData::class.java)
    private val builder = DefaultExchangeMetadataFetcher.Builder(
        supportedExchange = mock(),
        exchangeFactory = mock<ExchangeFactory>().apply {
            whenever(this.createExchange(any<ExchangeSpecification>())).thenReturn(mock())
        },
        preventFromLoadingDefaultXchangeMetadata = false,
        xchangeMetadataProvider = { _ -> sampleMetadata },
    )
    private val noApiKey: ExchangeApiKey? = null
    private val oneInchBtcCurrencyPair = CurrencyPair.of("1INCH/BTC")

    @Test
    fun shouldOverrideCurrencyMetadata() {
        // given
        val tested = builder.copy(
            overridenCurrencies = mapOf(
                "BTC" to CurrencyMetadataOverride(
                    minWithdrawalAmount = 2.0,
                    withdrawalFee = 10.0
                )
            )
        ).build()
        // when
        val exchangeMetadata = tested.fetchExchangeMetadata(noApiKey)
        // then
        assertThat(exchangeMetadata.currencyMetadata.getValue("BTC").minWithdrawalAmount).isEqualTo("2.0".toBigDecimal())
        assertThat(exchangeMetadata.currencyMetadata.getValue("BTC").withdrawalFeeAmount).isEqualTo("10.0".toBigDecimal())
    }

    @Test
    fun shouldProvideTradingFeeRangeWhenThereIsOnlyTradingFee() {
        // given
        val tested = builder.build()
        // when
        val exchangeMetadata = tested.fetchExchangeMetadata(noApiKey)
        // then
        assertThat(exchangeMetadata.currencyPairMetadata.getValue(oneInchBtcCurrencyPair).transactionFeeRanges)
            .isEqualTo(
                TransactionFeeRanges(
                    takerFees = listOf(TransactionFeeRange(beginAmount = BigDecimal.ZERO, feeRatio = "0.001".toBigDecimal())),
                    makerFees = listOf(TransactionFeeRange(beginAmount = BigDecimal.ZERO, feeRatio = "0.001".toBigDecimal())),
                )
            )
    }

}
