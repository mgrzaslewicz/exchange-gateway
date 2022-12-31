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
import biboxCurrencyMetadata
import biboxOverridenCurrencyPairMetadata
import org.knowm.xchange.Exchange
import org.knowm.xchange.dto.meta.ExchangeMetaData
import org.knowm.xchange.dto.meta.WalletHealth
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
internal fun preventFromLoadingStaticXchangeMetadata(es: XchangeExchangeSpecification) {
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
    val withdrawalFeeAmount: Double?,
    val minWithdrawalAmount: Double? = null,
    val isWithdrawalEnabled: Boolean? = null,
    val isDepositEnabled: Boolean? = null,
) {
    fun override(currencyMetadata: CurrencyMetadata): CurrencyMetadata {
        return currencyMetadata.copy(
            withdrawalFeeAmount = withdrawalFeeAmount?.toBigDecimal() ?: currencyMetadata.withdrawalFeeAmount,
            minWithdrawalAmount = minWithdrawalAmount?.toBigDecimal() ?: currencyMetadata.minWithdrawalAmount,
            withdrawalEnabled = isWithdrawalEnabled ?: currencyMetadata.withdrawalEnabled,
            depositEnabled = isDepositEnabled ?: currencyMetadata.depositEnabled,
        )
    }
}

data class CurrencyPairMetadataOverride(
    val transactionFeeRanges: TransactionFeeRanges?,
) {
    fun override(currencyPairMetadata: CurrencyPairMetadata): CurrencyPairMetadata {
        return currencyPairMetadata.copy(
            transactionFeeRanges = transactionFeeRanges ?: currencyPairMetadata.transactionFeeRanges,
        )
    }
}

fun WalletHealth.toDepositEnabled(): Boolean? {
    return when {
        this == WalletHealth.UNKNOWN -> null
        this == WalletHealth.ONLINE || this == WalletHealth.WITHDRAWALS_DISABLED -> true
        else -> false
    }
}

fun WalletHealth.toWithdrawalEnabled(): Boolean? {
    return when {
        this == WalletHealth.UNKNOWN -> null
        this == WalletHealth.ONLINE || this == WalletHealth.DEPOSITS_DISABLED -> true
        else -> false
    }
}

class DefaultExchangeMetadataFetcher private constructor(
    override val supportedExchange: SupportedExchange,
    private val exchangeFactory: XchangeExchangeFactory,
    private val preventFromLoadingDefaultXchangeMetadata: Boolean,
    private val xchangeSpecificationApiKeyAssigner: XchangeSpecificationApiKeyAssigner,
    private val xchangeMetadataProvider: (exchange: Exchange) -> ExchangeMetaData,
    private val currencyPairRename: Map<CurrencyPair, CurrencyPair>,
    private val overridenCurrencyMetadata: Map<String, CurrencyMetadataOverride>,
    private val overridenCurrencyPairMetadata: Map<CurrencyPair, CurrencyPairMetadataOverride>,
    /**
     * Used when xchange has no currency metadata at all
     */
    private val defaultCurrencyMetadata: Map<String, CurrencyMetadata>,
) : ExchangeMetadataFetcher {

    override fun fetchExchangeMetadata(apiKey: ExchangeApiKey?): ExchangeMetadata {
        val exchangeSpec = XchangeExchangeSpecification(supportedExchange.toXchangeJavaClass())
        xchangeSpecificationApiKeyAssigner.assignKeys(supportedExchange, exchangeSpec, apiKey)
        if (preventFromLoadingDefaultXchangeMetadata) {
            preventFromLoadingStaticXchangeMetadata(exchangeSpec)
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
                currencyPair to overrideCurrencyPairMetadataIfNeeded(
                    currencyPair = currencyPair,
                    currencyPairMetadata = CurrencyPairMetadata(
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
                )
            }.toMap()
        val currencies = xchangeMetadata.currencies?.map {
            it.key.currencyCode to overrideCurrencyMetadataIfNeeded(
                currencyCode = it.key.currencyCode,
                currencyMetadata = CurrencyMetadata(
                    scale = getScaleOrDefault(it.key, it.value, metadataWarnings),
                    withdrawalFeeAmount = it.value?.withdrawalFee,
                    minWithdrawalAmount = it.value?.minWithdrawalAmount,
                    depositEnabled = it.value?.walletHealth?.toDepositEnabled(),
                    withdrawalEnabled = it.value?.walletHealth?.toWithdrawalEnabled()
                )
            )
        }?.toMap() ?: defaultCurrencyMetadata
        if (currencies.isEmpty()) {
            metadataWarnings.add("Currency metadata is empty")
        }
        val exchangeMetadata = ExchangeMetadata(
            exchange = supportedExchange,
            currencyPairMetadata = currencyPairs,
            currencyMetadata = currencies,
            debugWarnings = metadataWarnings
        )
        return exchangeMetadata
    }

    private fun overrideCurrencyMetadataIfNeeded(currencyCode: String, currencyMetadata: CurrencyMetadata): CurrencyMetadata {
        return if (overridenCurrencyMetadata.containsKey(currencyCode)) {
            val overridenCurrencyMetadata = overridenCurrencyMetadata.getValue(currencyCode)
            return overridenCurrencyMetadata.override(currencyMetadata)
        } else {
            currencyMetadata
        }
    }

    private fun overrideCurrencyPairMetadataIfNeeded(currencyPair: CurrencyPair, currencyPairMetadata: CurrencyPairMetadata): CurrencyPairMetadata {
        return if (overridenCurrencyPairMetadata.containsKey(currencyPair)) {
            val overridenCurrencyPairMetadata = overridenCurrencyPairMetadata.getValue(currencyPair)
            return overridenCurrencyPairMetadata.override(currencyPairMetadata)
        } else {
            currencyPairMetadata
        }
    }

    data class Builder(
        var supportedExchange: SupportedExchange? = null,
        var exchangeFactory: XchangeExchangeFactory? = null,
        var preventFromLoadingStaticXchangeMetadata: Boolean = false,
        var xchangeSpecificationApiKeyAssigner: XchangeSpecificationApiKeyAssigner = XchangeSpecificationApiKeyAssigner(ExchangeSpecificationVerifier()),
        var xchangeMetadataProvider: (exchange: Exchange) -> ExchangeMetaData = { exchange -> exchange.exchangeMetaData },
        var currencyPairRename: Map<CurrencyPair, CurrencyPair> = emptyMap(),
        var overridenCurrencyMetadata: Map<String, CurrencyMetadataOverride> = emptyMap(),
        var overridenCurrencyPairMetadata: Map<CurrencyPair, CurrencyPairMetadataOverride> = emptyMap(),
        var defaultCurrencyMetadata: Map<String, CurrencyMetadata> = emptyMap(),
    ) {

        fun build(): DefaultExchangeMetadataFetcher {
            return DefaultExchangeMetadataFetcher(
                supportedExchange = supportedExchange!!,
                exchangeFactory = exchangeFactory!!,
                preventFromLoadingDefaultXchangeMetadata = preventFromLoadingStaticXchangeMetadata,
                xchangeSpecificationApiKeyAssigner = xchangeSpecificationApiKeyAssigner,
                xchangeMetadataProvider = xchangeMetadataProvider,
                currencyPairRename = currencyPairRename,
                overridenCurrencyMetadata = overridenCurrencyMetadata,
                overridenCurrencyPairMetadata = overridenCurrencyPairMetadata,
                defaultCurrencyMetadata = defaultCurrencyMetadata,
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
        defaultBuilder.copy(
            supportedExchange = BIBOX,
            defaultCurrencyMetadata = biboxCurrencyMetadata,
            overridenCurrencyPairMetadata = biboxOverridenCurrencyPairMetadata,
        ).build(),
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
            overridenCurrencyMetadata = krakenOverridenCurrencyMetadata
        ).build(),
        defaultBuilder.copy(
            exchangeFactory = exchangeFactory,
            supportedExchange = HITBTC, currencyPairRename = mapOf(
                CurrencyPair.of("REP/USD") to CurrencyPair.of("REP/USDT"),
                CurrencyPair.of("XRP/USD") to CurrencyPair.of("XRP/USDT")
            ),
            xchangeSpecificationApiKeyAssigner = xchangeSpecificationApiKeyAssigner,
            preventFromLoadingStaticXchangeMetadata = true
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
                preventFromLoadingStaticXchangeMetadata = true
            ).build()
        }

fun exchangeMetadataFetchers(exchangeFactory: XchangeExchangeFactory, xchangeSpecificationApiKeyAssigner: XchangeSpecificationApiKeyAssigner) =
    overridenExchangeMetadataFetchers(exchangeFactory, xchangeSpecificationApiKeyAssigner) + defaultExchangeMetadataFetchers(exchangeFactory, xchangeSpecificationApiKeyAssigner)
