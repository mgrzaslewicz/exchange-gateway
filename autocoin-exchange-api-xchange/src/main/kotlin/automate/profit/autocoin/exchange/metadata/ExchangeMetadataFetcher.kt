package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.SupportedExchange.*
import automate.profit.autocoin.exchange.apikey.ExchangeApiKey
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.metadata.binance.BinanceExchangeMetadataFetcher
import automate.profit.autocoin.exchange.metadata.bittrex.BittrexExchangeMetadataFetcher
import automate.profit.autocoin.exchange.metadata.kucoin.KucoinExchangeMetadataFetcher
import automate.profit.autocoin.exchange.peruser.toCurrencyPair
import automate.profit.autocoin.exchange.toXchangeJavaClass
import mu.KotlinLogging
import org.knowm.xchange.ExchangeFactory as XchangeExchangeFactory
import org.knowm.xchange.ExchangeSpecification as XchangeExchangeSpecification
import org.knowm.xchange.currency.Currency as XchangeCurrency
import org.knowm.xchange.dto.meta.CurrencyMetaData as XchangeCurrencyMetaData
import org.knowm.xchange.dto.meta.CurrencyPairMetaData as XchangeCurrencyPairMetaData
import java.io.File
import java.math.BigDecimal

class XchangeMetadataJson(val json: String)

interface ExchangeMetadataFetcher {
    val supportedExchange: SupportedExchange
    fun fetchExchangeMetadata(apiKey: ExchangeApiKey? = null): Pair<XchangeMetadataJson, ExchangeMetadata>
}

private val logger = KotlinLogging.logger {}
internal fun BigDecimal?.orMin() = this ?: 0.00000001.toBigDecimal()
internal fun BigDecimal?.orMax() = this ?: BigDecimal.valueOf(Long.MAX_VALUE)
internal val DEFAULT_SCALE = 8

internal fun numberOfDecimals(value: String): Int {
    return BigDecimal(value).stripTrailingZeros().scale()
}

internal fun XchangeExchangeSpecification.setApiKey(apiKey: ExchangeApiKey?) {
    if (apiKey != null) {
        this.apiKey = apiKey.publicKey
        this.secretKey = apiKey.secretKey
    }
}

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

internal fun getScaleOrDefault(supportedExchange: SupportedExchange, currency: XchangeCurrency, currencyMetaData: XchangeCurrencyMetaData?): Int {
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
internal fun preventFromLoadingDefaultXchangeMetadata(es: XchangeExchangeSpecification) {
    es.metaDataJsonFileOverride = getEmptyXchangeMetadataFile().absolutePath
}

internal fun XchangeCurrencyPairMetaData.getTransactionFeeRanges(
    defaultTakerFees: List<TransactionFeeRange> = emptyList(),
    defaultMakerFees: List<TransactionFeeRange> = emptyList()
): TransactionFeeRanges {
    return TransactionFeeRanges(
        takerFees = this.feeTiers?.map { feeTier ->
            TransactionFeeRange(
                beginAmount = feeTier.beginQuantity,
                fee = TransactionFee(percent = feeTier.fee.takerFee)
            )
        } ?: defaultTakerFees,
        makerFees = this.feeTiers?.map { feeTier ->
            TransactionFeeRange(
                beginAmount = feeTier.beginQuantity,
                fee = TransactionFee(percent = feeTier.fee.makerFee)
            )
        } ?: defaultMakerFees
    )
}

class DefaultExchangeMetadataFetcher(
    override val supportedExchange: SupportedExchange,
    private val exchangeFactory: XchangeExchangeFactory,
    private val preventFromLoadingDefaultXchangeMetadata: Boolean = true,
    private val currencyPairRename: Map<CurrencyPair, CurrencyPair> = emptyMap()
) : ExchangeMetadataFetcher {

    override fun fetchExchangeMetadata(apiKey: ExchangeApiKey?): Pair<XchangeMetadataJson, ExchangeMetadata> {
        val exchangeSpec = XchangeExchangeSpecification(supportedExchange.toXchangeJavaClass())
        exchangeSpec.setApiKey(apiKey)
        if (preventFromLoadingDefaultXchangeMetadata) {
            preventFromLoadingDefaultXchangeMetadata(exchangeSpec)
        }
        val exchange = exchangeFactory.createExchange(exchangeSpec)
        val xchangeMetadata = exchange.exchangeMetaData
        val xchangeMetadataJson = xchangeMetadata.toJSONString()

        val currencyPairs = xchangeMetadata.currencyPairs
            .filter {
                if (it.value == null) {
                    logger.warn { "$supportedExchange-${it.key} no currency pair in metadata, skipping" }
                }
                it.value != null
            }
            .map {
                val currencyPairBeforeRename = it.key.toCurrencyPair()
                val currencyPair = currencyPairRename.getOrDefault(currencyPairBeforeRename, currencyPairBeforeRename)
                if (currencyPair != currencyPairBeforeRename) {
                    logger.warn { "$supportedExchange-$currencyPairBeforeRename renamed to $currencyPair" }
                }
                if (it.value.priceScale == null) {
                    logger.warn { "$supportedExchange-${it.key} priceScale is not provided" }
                }
                currencyPair to CurrencyPairMetadata(
                    amountScale = it.value.priceScale ?: DEFAULT_SCALE,
                    priceScale = it.value.priceScale ?: DEFAULT_SCALE,
                    minimumAmount = it.value.minimumAmount.orMin(),
                    maximumAmount = it.value.maximumAmount.orMax(),
                    minimumOrderValue = BigDecimal.ZERO,
                    maximumPriceMultiplierUp = 10.toBigDecimal(),
                    maximumPriceMultiplierDown = 0.1.toBigDecimal(),
                    buyFeeMultiplier = BigDecimal.ZERO,
                    transactionFeeRanges = it.value.getTransactionFeeRanges()
                )
            }.toMap()
        val currencies = exchange.exchangeMetaData.currencies?.map {
            it.key.currencyCode to CurrencyMetadata(
                scale = getScaleOrDefault(supportedExchange, it.key, it.value)
            )
        }?.toMap() ?: emptyMap()
        if (currencies.isEmpty()) {
            logger.warn { "[$supportedExchange] Currency metadata is empty" }
        }
        val exchangeMetadata = ExchangeMetadata(
            currencyPairMetadata = currencyPairs,
            currencyMetadata = currencies
        )
        return Pair(XchangeMetadataJson(xchangeMetadataJson), exchangeMetadata)
    }
}

fun overridenExchangeMetadataFetchers(exchangeFactory: XchangeExchangeFactory) = listOf(
    BittrexExchangeMetadataFetcher(exchangeFactory),
    BinanceExchangeMetadataFetcher(exchangeFactory),
    KucoinExchangeMetadataFetcher(exchangeFactory),
    DefaultExchangeMetadataFetcher(supportedExchange = BITBAY, exchangeFactory = exchangeFactory, preventFromLoadingDefaultXchangeMetadata = false),
    DefaultExchangeMetadataFetcher(supportedExchange = BITSTAMP, exchangeFactory = exchangeFactory, preventFromLoadingDefaultXchangeMetadata = false),
    DefaultExchangeMetadataFetcher(supportedExchange = COINDEAL, exchangeFactory = exchangeFactory, preventFromLoadingDefaultXchangeMetadata = false),
    DefaultExchangeMetadataFetcher(supportedExchange = GEMINI, exchangeFactory = exchangeFactory, preventFromLoadingDefaultXchangeMetadata = false),
    DefaultExchangeMetadataFetcher(supportedExchange = IDEX, exchangeFactory = exchangeFactory, preventFromLoadingDefaultXchangeMetadata = false),
    DefaultExchangeMetadataFetcher(
        exchangeFactory = exchangeFactory,
        supportedExchange = HITBTC, currencyPairRename = mapOf(
            CurrencyPair.of("REP/USD") to CurrencyPair.of("REP/USDT"),
            CurrencyPair.of("XRP/USD") to CurrencyPair.of("XRP/USDT")
        )
    ),
    DefaultExchangeMetadataFetcher(LUNO, exchangeFactory = exchangeFactory, preventFromLoadingDefaultXchangeMetadata = false),
    DefaultExchangeMetadataFetcher(POLONIEX, exchangeFactory = exchangeFactory, preventFromLoadingDefaultXchangeMetadata = false)
)

fun defaultExchangeMetadataFetchers(exchangeFactory: XchangeExchangeFactory) =
    (SupportedExchange.values().toSet() - overridenExchangeMetadataFetchers(exchangeFactory)
        .map { it.supportedExchange }.toSet())
        .map { DefaultExchangeMetadataFetcher(supportedExchange = it, exchangeFactory = exchangeFactory) }

fun exchangeMetadataFetchers(exchangeFactory: XchangeExchangeFactory = XchangeExchangeFactory.INSTANCE) =
    overridenExchangeMetadataFetchers(exchangeFactory) + defaultExchangeMetadataFetchers(exchangeFactory)
