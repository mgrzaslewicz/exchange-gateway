package automate.profit.autocoin.exchange.metadata.bittrex

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.apikey.ExchangeApiKey
import automate.profit.autocoin.exchange.metadata.*
import automate.profit.autocoin.exchange.peruser.toCurrencyPair
import automate.profit.autocoin.exchange.toXchangeJavaClass
import mu.KotlinLogging
import org.knowm.xchange.Exchange
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.bittrex.BittrexAdapters
import org.knowm.xchange.bittrex.dto.marketdata.BittrexSymbol
import org.knowm.xchange.bittrex.service.BittrexMarketDataServiceRaw
import org.knowm.xchange.dto.meta.CurrencyPairMetaData
import org.knowm.xchange.dto.meta.ExchangeMetaData
import java.math.BigDecimal
import java.math.RoundingMode

class BittrexExchangeMetadataFetcher(
    private val exchangeFactory: ExchangeFactory,
    private val bittrexSymbolsProvider: (Exchange) -> List<BittrexSymbol> = { exchange -> (exchange.marketDataService as BittrexMarketDataServiceRaw).bittrexSymbols },
    private val xchangeMetadataProvider: (Exchange) -> ExchangeMetaData = { exchange -> exchange.exchangeMetaData }
) : ExchangeMetadataFetcher {
    private val logger = KotlinLogging.logger {}

    override val supportedExchange = SupportedExchange.BITTREX

    override fun fetchExchangeMetadata(apiKey: ExchangeApiKey?): Pair<XchangeMetadataJson, ExchangeMetadata> {
        val exchangeSpec = ExchangeSpecification(supportedExchange.toXchangeJavaClass())
        exchangeSpec.setApiKey(apiKey)
        preventFromLoadingDefaultXchangeMetadata(exchangeSpec)
        val exchange = exchangeFactory.createExchange(exchangeSpec)
        val bittrexSymbols = bittrexSymbolsProvider(exchange)
        val currencyPairs = BittrexAdapters.adaptCurrencyPairs(bittrexSymbols)
        val xchangeMetadata = xchangeMetadataProvider(exchange)
        val bittrexPriceScale = DEFAULT_SCALE

        val defaultTransactionFeeRanges = TransactionFeeRanges(
            makerFees = listOf(
                TransactionFeeRange(
                    beginAmount = BigDecimal.ZERO,
                    fee = TransactionFee(percent = "0.75".toBigDecimal())
                )
            ),
            takerFees = listOf(
                TransactionFeeRange(
                    beginAmount = BigDecimal.ZERO,
                    fee = TransactionFee(percent = "0.75".toBigDecimal())
                )
            )
        )

        currencyPairs.forEach { currencyPair ->
            xchangeMetadata.currencyPairs[currencyPair] = CurrencyPairMetaData(
                null,
                BigDecimal(bittrexSymbols
                    .first { it.quoteCurrencySymbol == currencyPair.counter && it.baseCurrencySymbol == currencyPair.base }
                    .minTradeSize.toDouble()
                ).setScale(8, RoundingMode.HALF_UP),
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
                        logger.debug { "$supportedExchange-${it.key} no currency pair in metadata, skipping" }
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
                        buyFeeMultiplier = BigDecimal("0.0025"), // https://bittrex.zendesk.com/hc/en-us/articles/115000199651-What-fees-does-Bittrex-charge-
                        transactionFeeRanges = it.value.getTransactionFeeRanges(
                            defaultMakerFees = defaultTransactionFeeRanges.makerFees,
                            defaultTakerFees = defaultTransactionFeeRanges.takerFees
                        )
                    )
                }.toMap(),
            currencyMetadata = xchangeMetadata.currencies.map {
                it.key.currencyCode to CurrencyMetadata(
                    scale = getScaleOrDefault(supportedExchange, it.key, it.value)
                )
            }.toMap()
        )
        return Pair(XchangeMetadataJson(xchangeMetadataJson), exchangeMetadata)
    }

}