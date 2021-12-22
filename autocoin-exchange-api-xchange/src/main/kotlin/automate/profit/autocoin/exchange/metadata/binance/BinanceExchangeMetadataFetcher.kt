package automate.profit.autocoin.exchange.metadata.binance

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.apikey.ExchangeApiKey
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.metadata.*
import automate.profit.autocoin.exchange.peruser.toCurrencyPair
import automate.profit.autocoin.exchange.toXchangeJavaClass
import mu.KotlinLogging
import org.knowm.xchange.binance.dto.marketdata.BinancePrice
import org.knowm.xchange.binance.dto.meta.exchangeinfo.BinanceExchangeInfo
import org.knowm.xchange.binance.service.BinanceMarketDataService
import org.knowm.xchange.dto.meta.ExchangeMetaData as XchangeExchangeMetaData
import java.math.BigDecimal
import org.knowm.xchange.Exchange as XchangeExchange
import org.knowm.xchange.ExchangeFactory as XchangeExchangeFactory
import org.knowm.xchange.ExchangeSpecification as XchangeExchangeSpecification

class BinanceExchangeMetadataFetcher(
    private val exchangeFactory: XchangeExchangeFactory,
    private val binanceExchangeInfoProvider: (XchangeExchange) -> BinanceExchangeInfo = { exchange -> (exchange.marketDataService as BinanceMarketDataService).exchangeInfo },
    private val binanceMetadataProvider: (XchangeExchange) -> XchangeExchangeMetaData = { exchange -> exchange.exchangeMetaData },
    private val binanceTickerProvider: (XchangeExchange) -> List<BinancePrice> = { exchange -> (exchange.marketDataService as BinanceMarketDataService).tickerAllPrices() }
) : ExchangeMetadataFetcher {
    override val supportedExchange = SupportedExchange.BINANCE

    private val logger = KotlinLogging.logger {}

    private val defaultTransactionFeeRanges = TransactionFeeRanges(
        makerFees = listOf(
            TransactionFeeRange(
                beginAmount = BigDecimal.ZERO,
                fee = TransactionFee(percent = "0.1".toBigDecimal())
            )
        ),
        takerFees = listOf(
            TransactionFeeRange(
                beginAmount = BigDecimal.ZERO,
                fee = TransactionFee(percent = "0.1".toBigDecimal())
            )
        )
    )

    override fun fetchExchangeMetadata(apiKey: ExchangeApiKey?): Pair<XchangeMetadataJson, ExchangeMetadata> {
        val exchangeSpec = XchangeExchangeSpecification(supportedExchange.toXchangeJavaClass())
        exchangeSpec.setApiKey(apiKey)
        preventFromLoadingDefaultXchangeMetadata(exchangeSpec)
        val exchange = exchangeFactory.createExchange(exchangeSpec)
        val binanceMetadata = binanceMetadataProvider(exchange)

        val exchangeInfo = binanceExchangeInfoProvider(exchange)
        val symbols = exchangeInfo.symbols
        val currencyPairsMap = mutableMapOf<CurrencyPair, CurrencyPairMetadata>()
        val currenciesMap = mutableMapOf<String, CurrencyMetadata>()

        val prices = binanceTickerProvider(exchange)
        for (price in prices) {
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
                        buyFeeMultiplier = BigDecimal.ZERO,
                        transactionFeeRanges = binanceMetadata.currencyPairs[price.currencyPair]?.getTransactionFeeRanges(
                            defaultTakerFees = defaultTransactionFeeRanges.takerFees,
                            defaultMakerFees = defaultTransactionFeeRanges.makerFees
                        ) ?: defaultTransactionFeeRanges
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
        val xchangeMetadataJson = binanceMetadata.toJSONString()
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
                logger.debug { "Unable to identify minimum order value for currency pair $currencyPair, will return default value $defaultValue. Mapping should be updated" }
                defaultValue
            }
        }
    }

}