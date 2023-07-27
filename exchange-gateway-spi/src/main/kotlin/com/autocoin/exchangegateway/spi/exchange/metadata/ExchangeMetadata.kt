package com.autocoin.exchangegateway.spi.exchange.metadata

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair


interface ExchangeMetadata {
    val exchange: Exchange
    val currencyPairMetadata: Map<out CurrencyPair, CurrencyPairMetadata>
    val currencyMetadata: Map<String, CurrencyMetadata>
    val warnings: List<String>

    fun currencies() = currencyMetadata.keys

    fun currencyPairs() = currencyPairMetadata.keys

}
