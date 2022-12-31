package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.peruser.toCurrencyPair
import org.knowm.xchange.Exchange


fun metadataFromExchange(exchange: Exchange): ExchangeMetadata {
    return ExchangeMetadata(
            currencyPairMetadata = exchange.exchangeMetaData.currencyPairs.map {
                it.key.toCurrencyPair() to CurrencyPairMetadata(
                        scale = it.value.priceScale,
                        minimumAmount = it.value.minimumAmount,
                        maximumAmount = it.value.maximumAmount
                )
            }.toMap(),
            currencyMetadata = exchange.exchangeMetaData.currencies.map {
                it.key.currencyCode to CurrencyMetadata(
                        scale = it.value.scale
                )
            }.toMap()
    )
}
