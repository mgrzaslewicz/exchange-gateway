package com.autocoin.exchangegateway.spi.exchange.metadata

import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair


interface ExchangeMetadata {
    val exchange: com.autocoin.exchangegateway.spi.exchange.ExchangeName
    val currencyPairMetadata: Map<out CurrencyPair, out CurrencyPairMetadata>
    val currencyMetadata: Map<String, out CurrencyMetadata>

    fun currencies() = currencyMetadata.keys

    fun currencyPairs() = currencyPairMetadata.keys

}
