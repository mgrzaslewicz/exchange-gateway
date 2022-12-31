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
import org.knowm.xchange.ExchangeFactory as XchangeExchangeFactory
import org.knowm.xchange.ExchangeSpecification as XchangeExchangeSpecification
import org.knowm.xchange.dto.meta.ExchangeMetaData
import org.knowm.xchange.kucoin.KucoinMarketDataService
import org.knowm.xchange.kucoin.dto.response.SymbolResponse
import org.knowm.xchange.kucoin.dto.response.TradeFeeResponse

class KucoinExchangeMetadataFetcherTest {
    private val kucoinSymbolsJson = this::class.java.getResource("/metadata/kucoin/kucoin-symbols-response.json").readText()
    private val kucoinMetadataJson = this::class.java.getResource("/metadata/kucoin/kucoin-metadata.json").readText()

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
                this.takerFeeRate = "0.1".toBigDecimal()
                this.makerFeeRate = "0.05".toBigDecimal()
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
        val feeRanges = exchangeMetadata.second.currencyPairMetadata.getValue(CurrencyPair.of("BTC/USDT")).transactionFeeRanges
        assertThat(feeRanges.takerFees).hasSize(1)
        assertThat(feeRanges.takerFees.first().fee.percent).isEqualTo("0.01".toBigDecimal())
        assertThat(feeRanges.makerFees.first().fee.percent).isEqualTo("0.005".toBigDecimal())
    }

    @Test
    fun shouldContainAllCurrencyPairs() {
        // when
        val exchangeMetadata = tested.fetchExchangeMetadata(apiKey = kucoinApiKey)
        // then
        assertThat(exchangeMetadata.second.currencyPairs()).hasSize(1110)
    }

    @Test
    fun shouldFetchTradingFees() {
        // when
        tested.fetchExchangeMetadata(apiKey = kucoinApiKey)
        // then
        verify(kucoinMarketDataService, times(111)).getKucoinTradeFee(any())
        verify(kucoinMarketDataService).getKucoinTradeFee("1EARTH-USDT,1INCH-USDT,2CRZ-BTC,2CRZ-USDT,AAVE-BTC,AAVE-KCS,AAVE-USDT,AAVE-UST,AAVE3L-USDT,AAVE3S-USDT")
    }
}