package com.autocoin.exchangegateway.api.exchange.metadata

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.metadata.CurrencyMetadata as SpiCurrencyMetadata
import com.autocoin.exchangegateway.spi.exchange.metadata.CurrencyPairMetadata as SpiCurrencyPairMetadata
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata as SpiExchangeMetadata

data class ExchangeMetadata(
    override val exchange: ExchangeName,
    override val currencyPairMetadata: Map<out CurrencyPair, SpiCurrencyPairMetadata>,
    override val currencyMetadata: Map<String, SpiCurrencyMetadata>,
    val debugWarnings: List<String>,
) : SpiExchangeMetadata
