package automate.profit.autocoin.exchange.metadata.kucoin

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.XchangeSpecificationApiKeyAssigner
import automate.profit.autocoin.exchange.apikey.ExchangeApiKey
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.metadata.*
import automate.profit.autocoin.exchange.peruser.toCurrencyPair
import automate.profit.autocoin.exchange.toXchangeJavaClass
import mu.KotlinLogging
import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.kucoin.KucoinAdapters
import org.knowm.xchange.kucoin.KucoinMarketDataService
import java.math.BigDecimal
import org.knowm.xchange.ExchangeFactory as XchangeExchangeFactory

class KucoinExchangeMetadataFetcher(
    private val exchangeFactory: XchangeExchangeFactory,
    private val xchangeSpecificationApiKeyAssigner: XchangeSpecificationApiKeyAssigner
) : ExchangeMetadataFetcher {
    private val logger = KotlinLogging.logger {}

    /**
     * https://docs.kucoin.com/#actual-fee-rate-of-the-trading-pair
     */
    private val maxCurrencyPairsPerRequest = 10
    override val supportedExchange = SupportedExchange.KUCOIN

    override fun fetchExchangeMetadata(apiKey: ExchangeApiKey?): Pair<XchangeMetadataJson, ExchangeMetadata> {
        val exchangeSpec = ExchangeSpecification(supportedExchange.toXchangeJavaClass())
        xchangeSpecificationApiKeyAssigner.assignKeys(SupportedExchange.KUCOIN, exchangeSpec, apiKey)
        preventFromLoadingDefaultXchangeMetadata(exchangeSpec)
        val xchangeExchange = exchangeFactory.createExchange(exchangeSpec)
        val xchangeMetadata = xchangeExchange.exchangeMetaData
        val xchangeMetadataJson = xchangeMetadata.toJSONString()
        val kucoinMarketDataService = xchangeExchange.marketDataService as KucoinMarketDataService
        val kucoinSymbols = kucoinMarketDataService.kucoinSymbols

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
        if (apiKey == null) {
            logger.warn { "[$supportedExchange] apiKey is missing, will not fetch trading fees" }
        } else {
            fillTradingFees(currencyPairsMap, kucoinMarketDataService)
        }
        val exchangeMetadata = ExchangeMetadata(
            currencyPairMetadata = currencyPairsMap,
            currencyMetadata = currenciesMap.toMap()
        )

        return Pair(XchangeMetadataJson(xchangeMetadataJson), exchangeMetadata)
    }

    private fun fillTradingFees(currencyPairsMap: MutableMap<CurrencyPair, CurrencyPairMetadata>, kucoinMarketDataService: KucoinMarketDataService) {
        currencyPairsMap.keys.toList()
            .sortedBy { it.toString() } // make order deterministic for unit tests
            .chunked(maxCurrencyPairsPerRequest)
            .forEach { currencyPairSublist ->
            val currencyPairsComaSeparated = currencyPairSublist.joinToString(",") { it.toStringWithSeparator('-') }
            val kucoinTradeFee = kucoinMarketDataService.getKucoinTradeFee(currencyPairsComaSeparated)
            kucoinTradeFee.forEach {
                /* sample response
        {
            "symbol": "BTC-USDT",
            "takerFeeRate": "0.001",
            "makerFeeRate": "0.001"
        },
                 */
                val currencyPair = KucoinAdapters.adaptCurrencyPair(it.symbol).toCurrencyPair()
                currencyPairsMap[currencyPair] = currencyPairsMap.getValue(currencyPair).copy(
                    transactionFeeRanges = TransactionFeeRanges(
                        takerFees = listOf(
                            TransactionFeeRange(
                                beginAmount = BigDecimal.ZERO,
                                fee = TransactionFee(percent = it.takerFeeRate.movePointLeft(1))
                            )
                        ),
                        makerFees = listOf(
                            TransactionFeeRange(
                                beginAmount = BigDecimal.ZERO,
                                fee = TransactionFee(percent = it.makerFeeRate.movePointLeft(1))
                            )
                        )
                    )
                )
            }
        }
    }

}