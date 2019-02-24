package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.currency.CurrencyPair

interface UserExchangeMetadataProvider {
    val exchangeMetadata: ExchangeMetadata
    fun currencyPairs(): Set<CurrencyPair>
    fun currencies(): Set<String>
    fun getCurrencyPairMetadata(currencyPair: CurrencyPair): CurrencyPairMetadata
}

class DefaultUserExchangeMetadataProvider(val exchangeName: String, override val exchangeMetadata: ExchangeMetadata) : UserExchangeMetadataProvider {
    override fun currencyPairs() = exchangeMetadata.currencyPairs()
    override fun currencies() = exchangeMetadata.currencies()
    override fun getCurrencyPairMetadata(currencyPair: CurrencyPair): CurrencyPairMetadata {
        return if (exchangeMetadata.currencyPairMetadata.containsKey(currencyPair)) {
            exchangeMetadata.currencyPairMetadata[currencyPair]!!
        } else throw IllegalArgumentException("Exchange $exchangeName has no $currencyPair in metadata")
    }
}
