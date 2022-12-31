package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.SupportedExchange.BINANCE
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.peruser.toCurrencyPair
import mu.KotlinLogging
import org.knowm.xchange.Exchange
import org.knowm.xchange.dto.meta.CurrencyPairMetaData
import java.math.BigDecimal

private fun BigDecimal?.orMax() = this ?: BigDecimal.valueOf(Long.MAX_VALUE)
private val logger = KotlinLogging.logger {}

private fun BigDecimal?.orMin() = this ?: 0.00000001.toBigDecimal()

fun metadataFromExchange(supportedExchange: SupportedExchange, exchange: Exchange): ExchangeMetadata {
    return ExchangeMetadata(
            currencyPairMetadata = exchange.exchangeMetaData.currencyPairs.map {
                it.key.toCurrencyPair() to CurrencyPairMetadata(
                        amountScale = getAmountScale(supportedExchange, it.value),
                        priceScale = it.value.priceScale,
                        minimumAmount = it.value.minimumAmount.orMin(),
                        maximumAmount = it.value.maximumAmount.orMax(),
                        minimumOrderValue = getMinimumOrderValue(supportedExchange, it.key.toCurrencyPair()),
                        maximumPriceMultiplierUp = 10.toBigDecimal(),
                        maximumPriceMultiplierDown = 0.1.toBigDecimal()
                )
            }.toMap(),
            currencyMetadata = exchange.exchangeMetaData.currencies.map {
                it.key.currencyCode to CurrencyMetadata(
                        scale = it.value.scale
                )
            }.toMap()
    )
}

fun getMinimumOrderValue(supportedExchange: SupportedExchange, currencyPair: CurrencyPair): BigDecimal {
    return when (supportedExchange) {
        BINANCE -> {
            /**
             * Based on https://support.binance.com/hc/en-us/articles/115000594711-Trading-Rule
             * It's "filterType": "MIN_NOTIONAL" in exchange metadata
             */
            when (currencyPair.counter) {
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
        else -> BigDecimal.ZERO
    }
}

fun getAmountScale(supportedExchange: SupportedExchange, currencyPairMetaData: CurrencyPairMetaData): Int {
    return when (supportedExchange) {
        BINANCE -> currencyPairMetaData.minimumAmount.scale()
        else -> currencyPairMetaData.priceScale
    }
}
