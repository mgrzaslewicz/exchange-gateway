package automate.profit.autocoin.exchange.metadata.bittrex

import automate.profit.autocoin.exchange.XchangeSpecificationApiKeyAssigner
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.metadata.metadataObjectMapper
import automate.profit.autocoin.exchange.peruser.ExchangeSpecificationVerifier
import com.fasterxml.jackson.core.type.TypeReference
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.bittrex.dto.marketdata.BittrexSymbol
import org.knowm.xchange.dto.meta.ExchangeMetaData

class BittrexExchangeMetadataFetcherTest {
    private val bittrexSymbolsJson = this::class.java.getResource("/metadata/bittrex/bittrex-symbols.json").readText()
    private val bittrexExchangeMetaDataJson = this::class.java.getResource("/metadata/bittrex/bittrex-exchange-metadata.json").readText()

    private object bittrexSymbolsType : TypeReference<List<BittrexSymbol>>()

    private val bittrexSymbols: List<BittrexSymbol> = metadataObjectMapper.readValue(bittrexSymbolsJson, bittrexSymbolsType)
    private val xchangeMetadata: ExchangeMetaData = metadataObjectMapper.readValue(bittrexExchangeMetaDataJson, ExchangeMetaData::class.java)


    private lateinit var tested: BittrexExchangeMetadataFetcher

    @BeforeEach
    fun setup() {
        val exchangeFactory: ExchangeFactory = mock<ExchangeFactory>().apply { whenever(this.createExchange(any<ExchangeSpecification>())).thenReturn(mock()) }
        tested = BittrexExchangeMetadataFetcher(
            exchangeFactory = exchangeFactory,
            bittrexSymbolsProvider = { _ -> bittrexSymbols },
            xchangeMetadataProvider = { _ -> xchangeMetadata },
            xchangeSpecificationApiKeyAssigner = XchangeSpecificationApiKeyAssigner(ExchangeSpecificationVerifier())
        )
    }

    @Test
    fun shouldCalculateFees() {
        // when
        val exchangeMetadata = tested.fetchExchangeMetadata(apiKey = null)
        // then
        val feeRanges = exchangeMetadata.currencyPairMetadata.getValue(CurrencyPair.of("ADA/BTC")).transactionFeeRanges
        assertThat(feeRanges.takerFees).hasSize(1)
        assertThat(feeRanges.takerFees.first().fee.rate).isEqualTo("0.0025".toBigDecimal())
        assertThat(feeRanges.makerFees.first().fee.rate).isEqualTo("0.0025".toBigDecimal())
    }

    @Test
    fun shouldContainAllCurrencyPairs() {
        // when
        val exchangeMetadata = tested.fetchExchangeMetadata(apiKey = null)
        // then
        assertThat(exchangeMetadata.currencyPairs()).hasSize(1111)
    }
}