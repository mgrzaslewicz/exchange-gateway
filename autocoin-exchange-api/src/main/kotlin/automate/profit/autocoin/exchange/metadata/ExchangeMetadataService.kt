package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.currency.CurrencyPair

interface ExchangeMetadataService {
    fun getMetadata(exchangeName: String): ExchangeMetadata
    fun getMetadata(exchangeName: String, currencyPair: CurrencyPair): CurrencyPairMetadata = getMetadata(exchangeName).currencyPairMetadata.getValue(currencyPair)
    fun getMetadata(exchangeName: String, currency: String): CurrencyMetadata = getMetadata(exchangeName).currencyMetadata.getValue(currency)
}
