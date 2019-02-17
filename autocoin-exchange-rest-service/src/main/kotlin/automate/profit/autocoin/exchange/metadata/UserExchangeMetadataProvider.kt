package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.currency.CurrencyPair
import mu.KLogging
import org.knowm.xchange.dto.meta.CurrencyMetaData
import org.knowm.xchange.dto.meta.CurrencyPairMetaData

interface UserExchangeMetadataProvider {
    fun getCurrencyPairMetadata(currencyPair: CurrencyPair): CurrencyPairMetaData
    fun getSupportedCurrencyPairs(): List<CurrencyPair>
    fun getCurrencyMetadata(currencyCode: String): CurrencyMetaData
}

val defaultCurrencyMetaData = CurrencyMetaData(8, null)
val defaultCurrencyPairMetaData = CurrencyPairMetaData(null, 0.00000001.toBigDecimal(), Double.MAX_VALUE.toBigDecimal(), 8, null)

class DefaultUserExchangeMetadataProvider(val exchangeName: String, private val exchangeMetadata: ExchangeMetadata) : UserExchangeMetadataProvider {
    private companion object : KLogging()

    override fun getSupportedCurrencyPairs(): List<CurrencyPair> = exchangeMetadata.supportedCurrencyPairs

    override fun getCurrencyMetadata(currencyCode: String): CurrencyMetaData {
        return if (exchangeMetadata.currencies.containsKey(currencyCode)) {
            val currencyMetadata = exchangeMetadata.currencies[currencyCode]
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

    override fun getCurrencyPairMetadata(currencyPair: CurrencyPair): CurrencyPairMetaData {
        return if (exchangeMetadata.currencyPairs.containsKey(currencyPair)) {
            val currencyPairMetaData = exchangeMetadata.currencyPairs[currencyPair]
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
