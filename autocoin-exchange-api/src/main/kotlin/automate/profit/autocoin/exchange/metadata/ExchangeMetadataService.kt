package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.currency.CurrencyPair

interface ExchangeMetadataService {
    fun getMetadata(exchangeName: String): ExchangeMetadata
    fun getCurrencyPairMetadata(exchangeName: String, currencyPair: CurrencyPair): CurrencyPairMetadata? = getMetadata(exchangeName).currencyPairMetadata[currencyPair]
    fun getCurrencyMetadata(exchangeName: String, currency: String): CurrencyMetadata? = getMetadata(exchangeName).currencyMetadata[currency]
}
