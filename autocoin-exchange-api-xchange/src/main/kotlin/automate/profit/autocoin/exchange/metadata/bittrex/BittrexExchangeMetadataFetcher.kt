package automate.profit.autocoin.exchange.metadata.bittrex

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.XchangeSpecificationApiKeyAssigner
import automate.profit.autocoin.exchange.apikey.ExchangeApiKey
import automate.profit.autocoin.exchange.metadata.*
import automate.profit.autocoin.exchange.peruser.ExchangeSpecificationVerifier
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
    private val xchangeMetadataProvider: (Exchange) -> ExchangeMetaData = { exchange -> exchange.exchangeMetaData },
    private val xchangeSpecificationApiKeyAssigner: XchangeSpecificationApiKeyAssigner = XchangeSpecificationApiKeyAssigner(ExchangeSpecificationVerifier())
) : ExchangeMetadataFetcher {
    private val logger = KotlinLogging.logger {}

    override val supportedExchange = SupportedExchange.BITTREX

    override fun fetchExchangeMetadata(apiKey: ExchangeApiKey?): ExchangeMetadata {
        val exchangeSpec = ExchangeSpecification(supportedExchange.toXchangeJavaClass())
        xchangeSpecificationApiKeyAssigner.assignKeys(SupportedExchange.BITTREX, exchangeSpec, apiKey)
        preventFromLoadingDefaultXchangeMetadata(exchangeSpec)
        val exchange = exchangeFactory.createExchange(exchangeSpec)
        val bittrexSymbols = bittrexSymbolsProvider(exchange)
        val currencyPairs = BittrexAdapters.adaptCurrencyPairs(bittrexSymbols)
        val xchangeMetadata = xchangeMetadataProvider(exchange)
        val bittrexPriceScale = DEFAULT_SCALE

        val debugWarnings = ArrayList<String>()
        debugWarnings.add("Trading fees are hardcoded based on https://bittrex.zendesk.com/hc/en-us/articles/115000199651-Bittrex-fees")
        val defaultTransactionFeeRanges = TransactionFeeRanges(
            makerFees = listOf(
                TransactionFeeRange(
                    beginAmount = BigDecimal.ZERO,
                    feeAmount = "0.0025".toBigDecimal()
                )
            ),
            takerFees = listOf(
                TransactionFeeRange(
                    beginAmount = BigDecimal.ZERO,
                    feeAmount = "0.0025".toBigDecimal()
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

        val exchangeMetadata = ExchangeMetadata(
            currencyPairMetadata = xchangeMetadata.currencyPairs
                .filter {
                    if (it.value == null) {
                        debugWarnings.add("${it.key} null currency pair value in bittrex metadata, excluded")
                    }
                    it.value != null
                }
                .map {
                    it.key.toCurrencyPair() to CurrencyPairMetadata(
                        amountScale = it.value.priceScale,
                        priceScale = it.value.priceScale,
                        minimumAmount = it.value.minimumAmount.orDefaultMin(),
                        maximumAmount = it.value.maximumAmount.orDefaultMax(),
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
                    scale = getScaleOrDefault(it.key, it.value, debugWarnings),
                    withdrawalFeeAmount = it.value?.withdrawalFee,
                    minWithdrawalAmount = it.value?.minWithdrawalAmount,
                    withdrawalEnabled = it.value?.walletHealth?.toWithdrawalEnabled(),
                    depositEnabled = it.value?.walletHealth?.toDepositEnabled(),
                    )
            }.toMap(),
            debugWarnings = debugWarnings
        )
        return exchangeMetadata
    }

}