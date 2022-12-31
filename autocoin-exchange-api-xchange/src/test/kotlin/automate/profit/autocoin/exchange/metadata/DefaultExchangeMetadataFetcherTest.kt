package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.XchangeSpecificationApiKeyAssigner
import automate.profit.autocoin.exchange.apikey.ExchangeApiKey
import automate.profit.autocoin.exchange.peruser.ExchangeSpecificationVerifier
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.dto.meta.ExchangeMetaData

class DefaultExchangeMetadataFetcherTest {
    private val sampleMetadataJson = this::class.java.getResource("/metadata/sample-metadata.json").readText()
    private val sampleMetadata: ExchangeMetaData = metadataObjectMapper.readValue(sampleMetadataJson, ExchangeMetaData::class.java)

    @Test
    fun shouldOverrideCurrencyMetadata() {
        // given
        val tested = DefaultExchangeMetadataFetcher(
            supportedExchange = mock(),
            exchangeFactory = mock<ExchangeFactory>().apply {
                whenever(this.createExchange(any<ExchangeSpecification>())).thenReturn(mock())
            },
            preventFromLoadingDefaultXchangeMetadata = false,
            xchangeSpecificationApiKeyAssigner = XchangeSpecificationApiKeyAssigner(exchangeSpecificationVerifier = ExchangeSpecificationVerifier()),
            xchangeMetadataProvider = { _ -> sampleMetadata },
            overridenCurrencies = mapOf(
                "BTC" to CurrencyMetadataOverride(
                    minWithdrawalAmount = 2.0,
                    withdrawalFee = 10.0
                )
            )
        )
        val noApiKey: ExchangeApiKey? = null
        // when
        val exchangeMetadata = tested.fetchExchangeMetadata(noApiKey)
        // then
        assertThat(exchangeMetadata.currencyMetadata.getValue("BTC").minWithdrawalAmount).isEqualTo("2.0".toBigDecimal())
        assertThat(exchangeMetadata.currencyMetadata.getValue("BTC").withdrawalFeeAmount).isEqualTo("10.0".toBigDecimal())
    }

}
