package automate.profit.autocoin.exchange.metadata.binance

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.metadata.metadataObjectMapper
import com.fasterxml.jackson.core.type.TypeReference
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.binance.dto.marketdata.BinancePrice
import org.knowm.xchange.binance.dto.meta.exchangeinfo.BinanceExchangeInfo
import org.knowm.xchange.dto.meta.ExchangeMetaData as XchangeExchangeMetaData

class BinanceExchangeMetadataFetcherTest {
    private val binanceMetadataJson = this::class.java.getResource("/metadata/binance/binance-metadata.json").readText()
    private val binanceExchangeInfoJson= this::class.java.getResource("/metadata/binance/binance-exchangeInfo.json").readText()
    private val binanceTickersJson = this::class.java.getResource("/metadata/binance/binance-tickers.json").readText()

    private object binanceTickersType : TypeReference<List<BinancePrice>>()

    private val binanceTickers: List<BinancePrice> = metadataObjectMapper.readValue(binanceTickersJson, binanceTickersType)
    private val binanceExchangeInfo: BinanceExchangeInfo = metadataObjectMapper.readValue(binanceExchangeInfoJson, BinanceExchangeInfo::class.java)
    private val binanceMetadata: XchangeExchangeMetaData = metadataObjectMapper.readValue(binanceMetadataJson, XchangeExchangeMetaData::class.java)


    private lateinit var tested: BinanceExchangeMetadataFetcher

    @BeforeEach
    fun setup() {
        val exchangeFactory: ExchangeFactory = mock<ExchangeFactory>().apply { whenever(this.createExchange(any<ExchangeSpecification>())).thenReturn(mock()) }
        tested = BinanceExchangeMetadataFetcher(
            exchangeFactory = exchangeFactory,
            binanceExchangeInfoProvider = { _ -> binanceExchangeInfo },
            binanceTickerProvider = { _ -> binanceTickers },
            binanceMetadataProvider = {_ -> binanceMetadata}
        )
    }

    @Test
    fun shouldCalculateFees() {
        // when
        val exchangeMetadata = tested.fetchExchangeMetadata(apiKey = null)
        // then
        val feeRanges = exchangeMetadata.second.currencyPairMetadata.getValue(CurrencyPair.of("ADA/BTC")).transactionFeeRanges
        assertThat(feeRanges.takerFees).hasSize(1)
        assertThat(feeRanges.takerFees.first().fee.percent).isEqualTo("0.1".toBigDecimal())
        assertThat(feeRanges.makerFees.first().fee.percent).isEqualTo("0.1".toBigDecimal())
    }

    @Test
    fun shouldContainAllCurrencyPairs() {
        // when
        val exchangeMetadata = tested.fetchExchangeMetadata(apiKey = null)
        // then
        assertThat(exchangeMetadata.second.currencyPairs()).hasSize(1846)
    }

}