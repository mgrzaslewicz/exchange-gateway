package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.currency.CurrencyPair
import mu.KLogging

interface UserExchangeMetadataProvider {
    fun getCurrencyPairMetadata(currencyPair: CurrencyPair): CurrencyPairMetadata
    fun getSupportedCurrencyPairs(): Set<CurrencyPair>
    fun getCurrencyMetadata(currencyCode: String): CurrencyMetadata
}

val defaultCurrencyMetaData = CurrencyMetadata(8)
val defaultCurrencyPairMetaData = CurrencyPairMetadata(
        scale = 8,
        minimumAmount = 0.00000001.toBigDecimal(),
        maximumAmount = 100.toBigDecimal()
)

class DefaultUserExchangeMetadataProvider(val exchangeName: String, private val exchangeMetadata: ExchangeMetadata) : UserExchangeMetadataProvider {
    private companion object : KLogging()

    override fun getSupportedCurrencyPairs(): Set<CurrencyPair> = exchangeMetadata.currencyPairs()

    override fun getCurrencyMetadata(currencyCode: String): CurrencyMetadata {
        return if (exchangeMetadata.currencyMetadata.containsKey(currencyCode)) {
            val currencyMetadata = exchangeMetadata.currencyMetadata[currencyCode]
            if (currencyMetadata != null) currencyMetadata
            else {
                logger.warn("Currency $currencyCode has null metadata, returning default one")
                defaultCurrencyMetaData
            }
        } else {
            logger.warn("Currency $currencyCode has no metadata, returning default one")
            defaultCurrencyMetaData
        }
    }

    override fun getCurrencyPairMetadata(currencyPair: CurrencyPair): CurrencyPairMetadata {
        return if (exchangeMetadata.currencyPairMetadata.containsKey(currencyPair)) {
            val currencyPairMetaData = exchangeMetadata.currencyPairMetadata[currencyPair]
            if (currencyPairMetaData != null) currencyPairMetaData
            else {
                logger.warn("Currency pair $currencyPair has null metadata, returning default one")
                defaultCurrencyPairMetaData
            }
        } else {
            logger.warn("Currency pair $currencyPair has no metadata, returning default one")
            defaultCurrencyPairMetaData
        }
    }

}
