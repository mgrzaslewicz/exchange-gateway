package automate.profit.autocoin.spi.exchange.metadata

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair


interface ExchangeMetadata {
    val exchange: ExchangeName
    val currencyPairMetadata: Map<out CurrencyPair, out CurrencyPairMetadata>
    val currencyMetadata: Map<String, out CurrencyMetadata>

    fun currencies() = currencyMetadata.keys

    fun currencyPairs() = currencyPairMetadata.keys

}
