package automate.profit.autocoin.exchange.metadata.kucoin

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.apikey.ExchangeApiKey
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.metadata.*
import automate.profit.autocoin.exchange.peruser.toCurrencyPair
import automate.profit.autocoin.exchange.toXchangeJavaClass
import mu.KotlinLogging
import org.knowm.xchange.ExchangeFactory as XchangeExchangeFactory
import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.kucoin.KucoinAdapters
import org.knowm.xchange.kucoin.KucoinMarketDataService
import java.math.BigDecimal

class KucoinExchangeMetadataFetcher(private val exchangeFactory: XchangeExchangeFactory) : ExchangeMetadataFetcher {
    private val logger = KotlinLogging.logger {}
    override val supportedExchange = SupportedExchange.KUCOIN

    override fun fetchExchangeMetadata(apiKey: ExchangeApiKey?): Pair<XchangeMetadataJson, ExchangeMetadata> {
        val exchangeSpec = ExchangeSpecification(supportedExchange.toXchangeJavaClass())
        exchangeSpec.setApiKey(apiKey)
        preventFromLoadingDefaultXchangeMetadata(exchangeSpec)
        val exchange = exchangeFactory.createExchange(exchangeSpec)
        val xchangeMetadata = exchange.exchangeMetaData
        val xchangeMetadataJson = xchangeMetadata.toJSONString()
        val kucoinSymbols = (exchange.marketDataService as KucoinMarketDataService).kucoinSymbols

        val currencyPairsMap = mutableMapOf<CurrencyPair, CurrencyPairMetadata>()
        val currenciesMap = mutableMapOf<String, CurrencyMetadata>()
        kucoinSymbols.forEach { symbol ->

            val xchangeCurrencyPair = KucoinAdapters.adaptCurrencyPair(symbol.symbol)

            val minSize = symbol.baseMinSize
            val maxSize = symbol.baseMaxSize
            val priceScale = symbol.priceIncrement.stripTrailingZeros().scale()

            val amountScale = symbol.baseIncrement.stripTrailingZeros().scale()
            val currencyPair = xchangeCurrencyPair.toCurrencyPair()

            currencyPairsMap[currencyPair] = CurrencyPairMetadata(
                amountScale = amountScale,
                priceScale = priceScale,
                minimumAmount = minSize.orMin(),
                maximumAmount = maxSize.orMax(),
                minimumOrderValue = BigDecimal.ZERO, // not present in kucoin api
                maximumPriceMultiplierUp = 1.2.toBigDecimal(), // not present in kucoin api
                maximumPriceMultiplierDown = 0.8.toBigDecimal(), // not present in kucoin api
                buyFeeMultiplier = BigDecimal("0.001"), // 0.1% https://www.kucoin.com/news/en-fee
                transactionFeeRanges = xchangeMetadata.currencyPairs[xchangeCurrencyPair]?.getTransactionFeeRanges() ?: TransactionFeeRanges()
            )
            currenciesMap[xchangeCurrencyPair.base.currencyCode] = CurrencyMetadata(
                scale = DEFAULT_SCALE
            )
            currenciesMap[xchangeCurrencyPair.counter.currencyCode] = CurrencyMetadata(
                scale = DEFAULT_SCALE
            )
        }
        val exchangeMetadata = ExchangeMetadata(
            currencyPairMetadata = currencyPairsMap,
            currencyMetadata = currenciesMap.toMap()
        )

        return Pair(XchangeMetadataJson(xchangeMetadataJson), exchangeMetadata)
    }

}