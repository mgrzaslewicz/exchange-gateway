package com.autocoin.exchangegateway.api.exchange.metadata.service.authorized

import com.autocoin.exchangegateway.api.exchange.currency.defaultXchangeCurrencyPairTransformer
import com.autocoin.exchangegateway.api.exchange.metadata.CurrencyMetadata
import com.autocoin.exchangegateway.api.exchange.metadata.CurrencyPairMetadata
import com.autocoin.exchangegateway.api.exchange.metadata.ExchangeMetadata
import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.metadata.service.authorized.AuthorizedMetadataService
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.meta.CurrencyPairMetaData
import org.knowm.xchange.dto.meta.ExchangeMetaData
import java.util.function.Function
import org.knowm.xchange.Exchange as XchangeExchange


/**
 * TODO provide transformers to cope with the fact that Xchange's metadata is often incomplete
 * currencyPairRenameTransformer
 * maximumAmountTransformer
 * minimumAmountTransformer
 * minimumOrderValueTransformer
 * maximumPriceMultiplierUpTransformer
 * maximumPriceMultiplierDownTransformer
 * buyFeeMultiplierTransformer
 * transactionFeeRangesTransformer
 * currencyDepositEnabled
 * currencyWithdrawalEnabled
 */
class XchangeAuthorizedMetadataService<T>(
    override val exchange: Exchange,
    override val apiKey: ApiKeySupplier<T>,
    val delegate: XchangeExchange,
    val metadataTransformers: List<ExchangeMetadataTransformer> = listOf(),
    private val xchangeCurrencyPairTransformer: Function<CurrencyPair, com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair> = defaultXchangeCurrencyPairTransformer,
) : AuthorizedMetadataService<T> {
    override fun getMetadata(): ExchangeMetadata {
        val result = ExchangeMetadata.Builder(exchange = exchange)
        val xchangeMetadata = delegate.exchangeMetaData
        val currencyPairsMetadata = getCurrencyPairsMetadata(xchangeMetadata)
        val currenciesMetadata = getCurrenciesMetadata(xchangeMetadata)
        result
            .withCurrencyPairMetadata(currencyPairsMetadata)
            .withCurrencyMetadata(currenciesMetadata)
        metadataTransformers.forEach {
            it(exchangeMetadataBuilder = result)
        }
        return result.build()
    }

    private fun getCurrenciesMetadata(xchangeMetadata: ExchangeMetaData) = xchangeMetadata.currencies
        ?.map { (xchangeCurrency, xchangeCurrencyMetaData) ->
            val currency = xchangeCurrency.currencyCode
            currency to CurrencyMetadata.Builder()
                .scale(xchangeCurrencyMetaData.scale)
                .withdrawalFeeAmount(xchangeCurrencyMetaData.withdrawalFee)
                .minWithdrawalAmount(xchangeCurrencyMetaData.minWithdrawalAmount)
        }
        ?.toMap() ?: emptyMap()

    private fun getCurrencyPairsMetadata(xchangeMetadata: ExchangeMetaData) = xchangeMetadata.currencyPairs
        .map { (xchangeCurrencyPair, xchangeCurrencyPairMetadata: CurrencyPairMetaData?) ->
            val currencyPair = xchangeCurrencyPairTransformer.apply(xchangeCurrencyPair)
            currencyPair to CurrencyPairMetadata.Builder(currencyPair = currencyPair)
                .amountScale(xchangeCurrencyPairMetadata?.baseScale)
                .priceScale(xchangeCurrencyPairMetadata?.priceScale)
                .minimumAmount(xchangeCurrencyPairMetadata?.minimumAmount)
                .maximumAmount(xchangeCurrencyPairMetadata?.maximumAmount)
        }.toMap()

}
