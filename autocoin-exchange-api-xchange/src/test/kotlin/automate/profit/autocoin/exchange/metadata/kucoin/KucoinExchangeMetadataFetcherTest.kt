package automate.profit.autocoin.exchange.metadata.kucoin

import automate.profit.autocoin.exchange.XchangeSpecificationApiKeyAssigner
import automate.profit.autocoin.exchange.apikey.ExchangeApiKey
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.metadata.metadataObjectMapper
import automate.profit.autocoin.exchange.peruser.ExchangeSpecificationVerifier
import com.fasterxml.jackson.core.type.TypeReference
import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.knowm.xchange.Exchange
import org.knowm.xchange.dto.meta.ExchangeMetaData
import org.knowm.xchange.kucoin.KucoinMarketDataService
import org.knowm.xchange.kucoin.dto.response.SymbolResponse
import org.knowm.xchange.kucoin.dto.response.TradeFeeResponse
import org.knowm.xchange.ExchangeFactory as XchangeExchangeFactory
import org.knowm.xchange.ExchangeSpecification as XchangeExchangeSpecification

class KucoinExchangeMetadataFetcherTest {
    private val kucoinSymbolsJson = this::class.java.getResource("/metadata/kucoin/kucoin-symbols-response.json").readText()
    private val kucoinMetadataJson = this::class.java.getResource("/metadata/kucoin/kucoin-metadata.json").readText()
    private val numberOfCyrrencyPairsInMetadata = 1110

    private object kucoinSymbolsResoponseType : TypeReference<List<SymbolResponse>>()

    private val kucoinSymbols: List<SymbolResponse> = metadataObjectMapper.readValue(kucoinSymbolsJson, kucoinSymbolsResoponseType)
    private val kucoinMetadata: ExchangeMetaData = metadataObjectMapper.readValue(kucoinMetadataJson, ExchangeMetaData::class.java)

    private val kucoinApiKey = ExchangeApiKey(
        publicKey = "some public key",
        secretKey = "some secret key",
        exchangeSpecificKeyParameters = mapOf("passphrase" to "some passphrase")
    )


    private lateinit var kucoinMarketDataService: KucoinMarketDataService
    private lateinit var tested: KucoinExchangeMetadataFetcher

    @BeforeEach
    fun setup() {
        kucoinMarketDataService = mock<KucoinMarketDataService>().apply {
            whenever(this.kucoinSymbols).thenReturn(this@KucoinExchangeMetadataFetcherTest.kucoinSymbols)
            whenever(this.getKucoinTradeFee(any())).thenReturn(listOf(TradeFeeResponse().apply {
                this.symbol = "BTC-USDT"
                this.takerFeeRate = "0.01".toBigDecimal()
                this.makerFeeRate = "0.005".toBigDecimal()
            }))
        }
        val xchangeExchange = mock<Exchange>().apply {
            whenever(this.marketDataService).thenReturn(kucoinMarketDataService)
            whenever(this.exchangeMetaData).thenReturn(kucoinMetadata)
        }
        val exchangeFactory: XchangeExchangeFactory = mock<XchangeExchangeFactory>().apply {
            whenever(this.createExchange(any<XchangeExchangeSpecification>())).thenReturn(xchangeExchange)
        }
        tested = KucoinExchangeMetadataFetcher(
            exchangeFactory = exchangeFactory,
            xchangeSpecificationApiKeyAssigner = XchangeSpecificationApiKeyAssigner(ExchangeSpecificationVerifier())
        )
    }

    @Test
    fun shouldCalculateFees() {
        // when
        val exchangeMetadata = tested.fetchExchangeMetadata(apiKey = kucoinApiKey)
        // then
        val feeRanges = exchangeMetadata.currencyPairMetadata.getValue(CurrencyPair.of("BTC/USDT")).transactionFeeRanges
        assertThat(feeRanges.takerFees).hasSize(1)
        assertThat(feeRanges.takerFees.first().feeAmount).isEqualTo("0.01".toBigDecimal())
        assertThat(feeRanges.makerFees.first().feeAmount).isEqualTo("0.005".toBigDecimal())
    }

    @Test
    fun shouldContainAllCurrencyPairs() {
        // when
        val exchangeMetadata = tested.fetchExchangeMetadata(apiKey = kucoinApiKey)
        // then
        assertThat(exchangeMetadata.currencyPairs()).hasSize(numberOfCyrrencyPairsInMetadata)
    }

    @Test
    fun shouldFetchTradingFees() {
        // when
        tested.fetchExchangeMetadata(apiKey = kucoinApiKey)
        // then
        verify(kucoinMarketDataService, times(numberOfCyrrencyPairsInMetadata / tested.maxCurrencyPairsPerTradeFeeRequest)).getKucoinTradeFee(any())
        verify(kucoinMarketDataService).getKucoinTradeFee("1EARTH-USDT")
    }
}
