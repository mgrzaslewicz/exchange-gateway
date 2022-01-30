package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.SupportedExchange.*
import automate.profit.autocoin.exchange.XchangeSpecificationApiKeyAssigner
import automate.profit.autocoin.exchange.apikey.ExchangeApiKey
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.metadata.binance.BinanceExchangeMetadataFetcher
import automate.profit.autocoin.exchange.metadata.bittrex.BittrexExchangeMetadataFetcher
import automate.profit.autocoin.exchange.metadata.kucoin.KucoinExchangeMetadataFetcher
import automate.profit.autocoin.exchange.peruser.ExchangeSpecificationVerifier
import automate.profit.autocoin.exchange.peruser.toCurrencyPair
import automate.profit.autocoin.exchange.toXchangeJavaClass
import mu.KotlinLogging
import org.knowm.xchange.Exchange
import org.knowm.xchange.dto.meta.ExchangeMetaData
import java.io.File
import java.math.BigDecimal
import org.knowm.xchange.ExchangeFactory as XchangeExchangeFactory
import org.knowm.xchange.ExchangeSpecification as XchangeExchangeSpecification
import org.knowm.xchange.currency.Currency as XchangeCurrency
import org.knowm.xchange.dto.meta.CurrencyMetaData as XchangeCurrencyMetaData
import org.knowm.xchange.dto.meta.CurrencyPairMetaData as XchangeCurrencyPairMetaData

interface ExchangeMetadataFetcher {
    val supportedExchange: SupportedExchange
    fun fetchExchangeMetadata(apiKey: ExchangeApiKey? = null): ExchangeMetadata
}

private val logger = KotlinLogging.logger {}
internal fun BigDecimal?.orDefaultMin() = this ?: 0.00000001.toBigDecimal()
internal fun BigDecimal?.orDefaultMax() = this ?: BigDecimal.valueOf(Long.MAX_VALUE)
internal val DEFAULT_SCALE = 8

internal fun numberOfDecimals(value: String): Int {
    return BigDecimal(value).stripTrailingZeros().scale()
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

internal fun getScaleOrDefault(
    currency: XchangeCurrency,
    currencyMetaData: XchangeCurrencyMetaData?,
    debugWarnings: ArrayList<String>
): Int {
    return if (currencyMetaData?.scale == null) {
        debugWarnings.add("$currency scale is null, returning default=$DEFAULT_SCALE")
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
    defaultMakerFees: List<TransactionFeeRange> = emptyList(),
    tradingFeeToTransactionFeeRangeFunction: (xchangeTradingFee: BigDecimal) -> TransactionFeeRange = { tradingFee ->
        TransactionFeeRange(
            beginAmount = BigDecimal.ZERO,
            feeRatio = tradingFee.movePointLeft(2) // org.knowm.xchange.dto.meta.CurrencyPairMetaData.tradingFee is fraction of percent, however in javadoc mentioned only as a fraction
        )
    }
): TransactionFeeRanges {
    return when {
        tradingFee != null -> TransactionFeeRanges(
            takerFees = listOf(tradingFeeToTransactionFeeRangeFunction(tradingFee)),
            makerFees = listOf(tradingFeeToTransactionFeeRangeFunction(tradingFee)),
        )
        feeTiers != null -> TransactionFeeRanges(
            takerFees = feeTiers.map { feeTier ->
                TransactionFeeRange(
                    beginAmount = feeTier.beginQuantity,
                    feeAmount = feeTier.fee.takerFee
                )
            },
            makerFees = feeTiers.map { feeTier ->
                TransactionFeeRange(
                    beginAmount = feeTier.beginQuantity,
                    feeAmount = feeTier.fee.makerFee
                )
            }
        )
        else -> TransactionFeeRanges(takerFees = defaultTakerFees, makerFees = defaultMakerFees)
    }
}

data class CurrencyMetadataOverride(
    val withdrawalFee: Double?,
    val minWithdrawalAmount: Double?
)

class DefaultExchangeMetadataFetcher private constructor(
    override val supportedExchange: SupportedExchange,
    private val exchangeFactory: XchangeExchangeFactory,
    private val preventFromLoadingDefaultXchangeMetadata: Boolean,
    private val xchangeSpecificationApiKeyAssigner: XchangeSpecificationApiKeyAssigner,
    private val xchangeMetadataProvider: (exchange: Exchange) -> ExchangeMetaData,
    private val currencyPairRename: Map<CurrencyPair, CurrencyPair>,
    private val overridenCurrencies: Map<String, CurrencyMetadataOverride>,
) : ExchangeMetadataFetcher {

    override fun fetchExchangeMetadata(apiKey: ExchangeApiKey?): ExchangeMetadata {
        val exchangeSpec = XchangeExchangeSpecification(supportedExchange.toXchangeJavaClass())
        xchangeSpecificationApiKeyAssigner.assignKeys(supportedExchange, exchangeSpec, apiKey)
        if (preventFromLoadingDefaultXchangeMetadata) {
            preventFromLoadingDefaultXchangeMetadata(exchangeSpec)
        }
        val exchange = exchangeFactory.createExchange(exchangeSpec)
        val xchangeMetadata = xchangeMetadataProvider(exchange)

        val metadataWarnings = ArrayList<String>()
        val currencyPairs = xchangeMetadata.currencyPairs
            .filter {
                if (it.value == null) {
                    metadataWarnings.add("$supportedExchange-${it.key} no currency pair in metadata, skipping")
                }
                it.value != null
            }
            .map {
                val currencyPairBeforeRename = it.key.toCurrencyPair()
                val currencyPair = currencyPairRename.getOrDefault(currencyPairBeforeRename, currencyPairBeforeRename)
                if (currencyPair != currencyPairBeforeRename) {
                    metadataWarnings.add("$currencyPairBeforeRename renamed to $currencyPair")
                }
                if (it.value.priceScale == null) {
                    metadataWarnings.add("${it.key} priceScale is not provided")
                }
                currencyPair to CurrencyPairMetadata(
                    amountScale = it.value.priceScale ?: DEFAULT_SCALE,
                    priceScale = it.value.priceScale ?: DEFAULT_SCALE,
                    minimumAmount = it.value.minimumAmount.orDefaultMin(),
                    maximumAmount = it.value.maximumAmount.orDefaultMax(),
                    minimumOrderValue = BigDecimal.ZERO,
                    maximumPriceMultiplierUp = 10.toBigDecimal(),
                    maximumPriceMultiplierDown = 0.1.toBigDecimal(),
                    buyFeeMultiplier = BigDecimal.ZERO,
                    transactionFeeRanges = it.value.getTransactionFeeRanges()
                )
            }.toMap()
        val currencies = xchangeMetadata.currencies?.map {
            it.key.currencyCode to overrideCurrencyMetadataIfNeeded(
                currencyCode = it.key.currencyCode,
                currencyMetadata = CurrencyMetadata(
                    scale = getScaleOrDefault(it.key, it.value, metadataWarnings),
                    withdrawalFeeAmount = it.value?.withdrawalFee,
                    minWithdrawalAmount = it.value?.minWithdrawalAmount
                )
            )
        }?.toMap() ?: emptyMap()
        if (currencies.isEmpty()) {
            metadataWarnings.add("Currency metadata is empty")
        }
        val exchangeMetadata = ExchangeMetadata(
            currencyPairMetadata = currencyPairs,
            currencyMetadata = currencies,
            debugWarnings = metadataWarnings
        )
        return exchangeMetadata
    }

    private fun overrideCurrencyMetadataIfNeeded(currencyCode: String, currencyMetadata: CurrencyMetadata): CurrencyMetadata {
        return if (overridenCurrencies.containsKey(currencyCode)) {
            val overridenCurrencyMetadata = overridenCurrencies.getValue(currencyCode)
            return currencyMetadata.copy(
                withdrawalFeeAmount = overridenCurrencyMetadata.withdrawalFee?.toBigDecimal() ?: currencyMetadata.withdrawalFeeAmount,
                minWithdrawalAmount = overridenCurrencyMetadata.minWithdrawalAmount?.toBigDecimal() ?: currencyMetadata.minWithdrawalAmount
            )
        } else {
            currencyMetadata
        }
    }

    data class Builder(
        var supportedExchange: SupportedExchange? = null,
        var exchangeFactory: XchangeExchangeFactory? = null,
        var preventFromLoadingDefaultXchangeMetadata: Boolean = false,
        var xchangeSpecificationApiKeyAssigner: XchangeSpecificationApiKeyAssigner = XchangeSpecificationApiKeyAssigner(ExchangeSpecificationVerifier()),
        var xchangeMetadataProvider: (exchange: Exchange) -> ExchangeMetaData = { exchange -> exchange.exchangeMetaData },
        var currencyPairRename: Map<CurrencyPair, CurrencyPair> = emptyMap(),
        var overridenCurrencies: Map<String, CurrencyMetadataOverride> = emptyMap(),
    ) {

        fun build(): DefaultExchangeMetadataFetcher {
            return DefaultExchangeMetadataFetcher(
                supportedExchange = supportedExchange!!,
                exchangeFactory = exchangeFactory!!,
                preventFromLoadingDefaultXchangeMetadata = preventFromLoadingDefaultXchangeMetadata,
                xchangeSpecificationApiKeyAssigner = xchangeSpecificationApiKeyAssigner,
                xchangeMetadataProvider = xchangeMetadataProvider,
                currencyPairRename = currencyPairRename,
                overridenCurrencies = overridenCurrencies
            )
        }
    }
}

fun overridenExchangeMetadataFetchers(
    exchangeFactory: XchangeExchangeFactory,
    xchangeSpecificationApiKeyAssigner: XchangeSpecificationApiKeyAssigner
): List<ExchangeMetadataFetcher> {
    val defaultBuilder = DefaultExchangeMetadataFetcher.Builder(
        exchangeFactory = exchangeFactory,
        xchangeSpecificationApiKeyAssigner = xchangeSpecificationApiKeyAssigner,
    )
    return listOf(
        BittrexExchangeMetadataFetcher(
            exchangeFactory = exchangeFactory,
            xchangeSpecificationApiKeyAssigner = xchangeSpecificationApiKeyAssigner
        ),
        BinanceExchangeMetadataFetcher(
            exchangeFactory = exchangeFactory,
            xchangeSpecificationApiKeyAssigner = xchangeSpecificationApiKeyAssigner
        ),
        KucoinExchangeMetadataFetcher(
            exchangeFactory = exchangeFactory,
            xchangeSpecificationApiKeyAssigner = xchangeSpecificationApiKeyAssigner
        ),
        defaultBuilder.copy(
            supportedExchange = BITBAY,
        ).build(),
        defaultBuilder.copy(
            supportedExchange = BITSTAMP,
        ).build(),
        defaultBuilder.copy(
            supportedExchange = COINDEAL,
        ).build(),
        defaultBuilder.copy(
            supportedExchange = GATEIO,
        ).build(),
        defaultBuilder.copy(
            supportedExchange = GEMINI,
        ).build(),
        defaultBuilder.copy(
            supportedExchange = IDEX,
        ).build(),
        defaultBuilder.copy(
            supportedExchange = KRAKEN,
            overridenCurrencies = krakenOverridenCurrenciesMetadata
        ).build(),
        defaultBuilder.copy(
            exchangeFactory = exchangeFactory,
            supportedExchange = HITBTC, currencyPairRename = mapOf(
                CurrencyPair.of("REP/USD") to CurrencyPair.of("REP/USDT"),
                CurrencyPair.of("XRP/USD") to CurrencyPair.of("XRP/USDT")
            ),
            xchangeSpecificationApiKeyAssigner = xchangeSpecificationApiKeyAssigner,
            preventFromLoadingDefaultXchangeMetadata = true
        ).build(),
        defaultBuilder.copy(
            supportedExchange = LUNO,
        ).build(),
        defaultBuilder.copy(
            supportedExchange = POLONIEX,
        ).build()
    )
}

fun defaultExchangeMetadataFetchers(exchangeFactory: XchangeExchangeFactory, xchangeSpecificationApiKeyAssigner: XchangeSpecificationApiKeyAssigner) =
    (SupportedExchange.values().toSet() - overridenExchangeMetadataFetchers(exchangeFactory, xchangeSpecificationApiKeyAssigner)
        .map { it.supportedExchange }.toSet())
        .map {
            DefaultExchangeMetadataFetcher.Builder(
                supportedExchange = it,
                exchangeFactory = exchangeFactory,
                xchangeSpecificationApiKeyAssigner = xchangeSpecificationApiKeyAssigner,
                preventFromLoadingDefaultXchangeMetadata = true
            ).build()
        }

fun exchangeMetadataFetchers(exchangeFactory: XchangeExchangeFactory, xchangeSpecificationApiKeyAssigner: XchangeSpecificationApiKeyAssigner) =
    overridenExchangeMetadataFetchers(exchangeFactory, xchangeSpecificationApiKeyAssigner) + defaultExchangeMetadataFetchers(exchangeFactory, xchangeSpecificationApiKeyAssigner)
