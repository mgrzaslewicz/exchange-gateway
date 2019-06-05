package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.SupportedExchange.*
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.peruser.toCurrencyPair
import automate.profit.autocoin.exchange.toXchangeClass
import mu.KotlinLogging
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.binance.service.BinanceMarketDataService
import org.knowm.xchange.bittrex.BittrexAdapters
import org.knowm.xchange.bittrex.service.BittrexMarketDataServiceRaw
import org.knowm.xchange.currency.Currency
import org.knowm.xchange.currency.CurrencyPair as XchangeCurrencyPair
import org.knowm.xchange.dto.meta.CurrencyMetaData
import org.knowm.xchange.dto.meta.CurrencyPairMetaData
import java.io.File
import java.math.BigDecimal
import java.math.BigDecimal.ROUND_HALF_UP

class XchangeMetadataJson(val json: String)

interface ExchangeMetadataFetcher {
    val supportedExchange: SupportedExchange
    fun fetchExchangeMetadata(): Pair<XchangeMetadataJson, ExchangeMetadata>
}

private val logger = KotlinLogging.logger {}
private fun BigDecimal?.orMin() = this ?: 0.00000001.toBigDecimal()
private fun BigDecimal?.orMax() = this ?: BigDecimal.valueOf(Long.MAX_VALUE)
private val DEFAULT_SCALE = 8

private val emptyXchangeMetadataFile = """
{
  "currency_pairs": {
  },
  "currencies": {
  },
  "public_rate_limits": [
    {
    }
  ]
}
""".trimIndent()

private fun getEmptyXchangeMetadataFile(): File {
    val emptyMetadataFile = File.createTempFile("empty-xchange-metadata", "json")
    emptyMetadataFile.writeText(emptyXchangeMetadataFile)
    return emptyMetadataFile
}

private fun getScaleOrDefault(supportedExchange: SupportedExchange, currency: Currency, currencyMetaData: CurrencyMetaData?): Int {
    return if (currencyMetaData?.scale == null) {
        logger.warn { "$supportedExchange-$currency scale is null, returning default=$DEFAULT_SCALE" }
        DEFAULT_SCALE
    } else {
        currencyMetaData.scale
    }
}

/**
 * @see BaseExchange.applySpecification
 */
private fun preventFromLoadingStaticJsonFile(es: ExchangeSpecification) {
    es.metaDataJsonFileOverride = getEmptyXchangeMetadataFile().absolutePath
}

class BittrexExchangeMetadataFetcher : ExchangeMetadataFetcher {
    private val logger = KotlinLogging.logger {}

    override val supportedExchange = BITTREX

    override fun fetchExchangeMetadata(): Pair<XchangeMetadataJson, ExchangeMetadata> {
        val exchangeSpec = ExchangeSpecification(supportedExchange.toXchangeClass().java)
        preventFromLoadingStaticJsonFile(exchangeSpec)
        val exchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpec)
        val bittrexSymbols = (exchange.marketDataService as BittrexMarketDataServiceRaw).bittrexSymbols
        val currencyPairs = BittrexAdapters.adaptCurrencyPairs(bittrexSymbols)
        val xchangeMetadata = exchange.exchangeMetaData
        val bittrexPriceScale = DEFAULT_SCALE
        currencyPairs.forEach { currencyPair ->
            xchangeMetadata.currencyPairs[currencyPair] = CurrencyPairMetaData(
                    null,
                    BigDecimal(bittrexSymbols.first { XchangeCurrencyPair(it.marketCurrency, it.baseCurrency) == currencyPair }.minTradeSize.toDouble()).setScale(8, ROUND_HALF_UP),
                    null,
                    bittrexPriceScale,
                    null
            )
            xchangeMetadata.currencies[currencyPair.base] = null
            xchangeMetadata.currencies[currencyPair.counter] = null
        }

        val xchangeMetadataJson = xchangeMetadata.toJSONString()
        val exchangeMetadata = ExchangeMetadata(
                currencyPairMetadata = xchangeMetadata.currencyPairs
                        .filter {
                            if (it.value == null) {
                                logger.warn { "$supportedExchange-${it.key} no currency pair in metadata, skipping" }
                            }
                            it.value != null
                        }
                        .map {
                            it.key.toCurrencyPair() to CurrencyPairMetadata(
                                    amountScale = it.value.priceScale,
                                    priceScale = it.value.priceScale,
                                    minimumAmount = it.value.minimumAmount.orMin(),
                                    maximumAmount = it.value.maximumAmount.orMax(),
                                    minimumOrderValue = BigDecimal.ZERO,
                                    maximumPriceMultiplierUp = 10.toBigDecimal(),
                                    maximumPriceMultiplierDown = 0.1.toBigDecimal(),
                                    buyFeeMultiplier = BigDecimal("0.0025")
                            )
                        }.toMap(),
                currencyMetadata = exchange.exchangeMetaData.currencies.map {
                    it.key.currencyCode to CurrencyMetadata(
                            scale = getScaleOrDefault(supportedExchange, it.key, it.value)
                    )
                }.toMap()
        )
        return Pair(XchangeMetadataJson(xchangeMetadataJson), exchangeMetadata)
    }

}

class BinanceExchangeMetadataFetcher : ExchangeMetadataFetcher {
    override val supportedExchange = BINANCE

    private val logger = KotlinLogging.logger {}

    private fun numberOfDecimals(value: String): Int {
        return BigDecimal(value).stripTrailingZeros().scale()
    }

    override fun fetchExchangeMetadata(): Pair<XchangeMetadataJson, ExchangeMetadata> {
        val exchangeSpec = ExchangeSpecification(supportedExchange.toXchangeClass().java)
        preventFromLoadingStaticJsonFile(exchangeSpec)
        val exchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpec)
        val xchangeMetadata = exchange.exchangeMetaData

        val marketDataService = exchange.marketDataService as BinanceMarketDataService
        val exchangeInfo = marketDataService.exchangeInfo
        val symbols = exchangeInfo.symbols
        val currencyPairsMap = mutableMapOf<CurrencyPair, CurrencyPairMetadata>()
        val currenciesMap = mutableMapOf<String, CurrencyMetadata>()

        for (price in marketDataService.tickerAllPrices()) {
            val pair = price.currencyPair

            for (symbol in symbols) {
                if (symbol.symbol == pair.base.currencyCode + pair.counter.currencyCode) {

                    val basePrecision = Integer.parseInt(symbol.baseAssetPrecision)
                    val counterPrecision = Integer.parseInt(symbol.quotePrecision)
                    var pairPrecision = 8
                    var amountPrecision = 8

                    var minQty: BigDecimal? = null
                    var maxQty: BigDecimal? = null
                    var stepSize: BigDecimal? = null

                    val filters = symbol.filters

                    for (filter in filters) {
                        if (filter.filterType == "PRICE_FILTER") {
                            pairPrecision = Math.min(pairPrecision, numberOfDecimals(filter.tickSize))
                        } else if (filter.filterType == "LOT_SIZE") {
                            amountPrecision = Math.min(amountPrecision, numberOfDecimals(filter.minQty))
                            minQty = BigDecimal(filter.minQty).stripTrailingZeros()
                            maxQty = BigDecimal(filter.maxQty).stripTrailingZeros()
                            stepSize = BigDecimal(filter.stepSize).stripTrailingZeros()
                        }
                    }
                    val currencyPair = price.currencyPair.toCurrencyPair()
                    currencyPairsMap[currencyPair] = CurrencyPairMetadata(
                            amountScale = amountPrecision,
                            priceScale = pairPrecision,
                            minimumAmount = minQty.orMin(),
                            maximumAmount = maxQty.orMax(),
                            minimumOrderValue = getMinimumOrderValue(currencyPair),
                            maximumPriceMultiplierUp = 1.2.toBigDecimal(),
                            maximumPriceMultiplierDown = 0.8.toBigDecimal(),
                            buyFeeMultiplier = BigDecimal.ZERO
                    )
                    currenciesMap[pair.base.currencyCode] = CurrencyMetadata(
                            scale = basePrecision
                    )
                    currenciesMap[pair.counter.currencyCode] = CurrencyMetadata(
                            scale = counterPrecision
                    )
                }
            }
        }
        val exchangeMetadata = ExchangeMetadata(
                currencyPairMetadata = currencyPairsMap,
                currencyMetadata = currenciesMap.toMap()
        )
        val xchangeMetadataJson = xchangeMetadata.toJSONString()
        return Pair(XchangeMetadataJson(xchangeMetadataJson), exchangeMetadata)
    }


    private fun getMinimumOrderValue(currencyPair: CurrencyPair): BigDecimal {
        /**
         * Based on https://support.binance.com/hc/en-us/articles/115000594711-Trading-Rule
         * It's "filterType": "MIN_NOTIONAL" in exchange metadata
         */
        return when (currencyPair.counter) {
            "USDT" -> 10.0.toBigDecimal()
            "PAX" -> 10.0.toBigDecimal()
            "TUSD" -> 10.0.toBigDecimal()
            "USDC" -> 10.0.toBigDecimal()
            "USDS" -> 10.0.toBigDecimal()
            "BTC" -> 0.001.toBigDecimal()
            "ETH" -> 0.01.toBigDecimal()
            "BNB" -> 1.0.toBigDecimal()
            else -> {
                val defaultValue = 0.001.toBigDecimal()
                logger.error { "Unable to identify minimum order value for currency pair $currencyPair, will return default value $defaultValue. Mapping should be updated" }
                defaultValue
            }
        }
    }

}

class KucoinExchangeMetadataFetcher : ExchangeMetadataFetcher {
    private val logger = KotlinLogging.logger {}
    override val supportedExchange = KUCOIN

    override fun fetchExchangeMetadata(): Pair<XchangeMetadataJson, ExchangeMetadata> {
        val exchangeSpec = ExchangeSpecification(supportedExchange.toXchangeClass().java)
        preventFromLoadingStaticJsonFile(exchangeSpec)
        val exchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpec)
        val xchangeMetadata = exchange.exchangeMetaData
        val xchangeMetadataJson = xchangeMetadata.toJSONString()
        val exchangeMetadata = ExchangeMetadata(
                currencyPairMetadata = xchangeMetadata.currencyPairs
                        .filter {
                            if (it.value == null) {
                                logger.warn { "$supportedExchange-${it.key} no currency pair in metadata, skipping" }
                            }
                            it.value != null
                        }
                        .map {
                            val currencyPair = it.key.toCurrencyPair()
                            currencyPair to CurrencyPairMetadata(
                                    amountScale = it.value.priceScale,
                                    priceScale = it.value.priceScale,
                                    minimumAmount = it.value.minimumAmount.orMin(),
                                    maximumAmount = it.value.maximumAmount.orMax(),
                                    minimumOrderValue = BigDecimal.ZERO,
                                    maximumPriceMultiplierUp = 10.toBigDecimal(),
                                    maximumPriceMultiplierDown = 0.1.toBigDecimal(),
                                    buyFeeMultiplier = BigDecimal.ZERO
                            )
                        }.toMap(),
                currencyMetadata = exchange.exchangeMetaData.currencies.map {
                    it.key.currencyCode to CurrencyMetadata(
                            scale = getScaleOrDefault(supportedExchange, it.key, it.value)
                    )
                }.toMap()
        )
        return Pair(XchangeMetadataJson(xchangeMetadataJson), exchangeMetadata)
    }

}

val exchangeMetadataFetchers = listOf(
        BittrexExchangeMetadataFetcher(),
        BinanceExchangeMetadataFetcher(),
        KucoinExchangeMetadataFetcher()
)