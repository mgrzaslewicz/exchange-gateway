package com.autocoin.exchangegateway.api.exchange.metadata

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import java.util.*
import com.autocoin.exchangegateway.spi.exchange.metadata.CurrencyMetadata as SpiCurrencyMetadata
import com.autocoin.exchangegateway.spi.exchange.metadata.CurrencyPairMetadata as SpiCurrencyPairMetadata
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata as SpiExchangeMetadata

data class ExchangeMetadata(
    override val exchange: Exchange,
    override val currencyPairMetadata: Map<CurrencyPair, SpiCurrencyPairMetadata>,
    override val currencyMetadata: Map<String, SpiCurrencyMetadata>,
    override val warnings: List<String>,
) : SpiExchangeMetadata {
    class Builder(
        private var exchange: Exchange,
    ) {
        var currencyPairMetadata: MutableMap<CurrencyPair, CurrencyPairMetadata.Builder> = mutableMapOf()
        var currencyMetadata: MutableMap<String, CurrencyMetadata.Builder> = mutableMapOf()
        var debugWarnings: MutableList<String> = mutableListOf()

        fun withCurrencyPairMetadata(currencyPairMetadata: Map<CurrencyPair, CurrencyPairMetadata.Builder>) = apply { this.currencyPairMetadata.putAll(currencyPairMetadata) }

        fun withCurrencyMetadata(currencyMetadata: Map<String, CurrencyMetadata.Builder>) = apply { this.currencyMetadata.putAll(currencyMetadata) }

        fun build() = ExchangeMetadata(
            exchange = exchange,
            currencyPairMetadata = Collections.unmodifiableMap(currencyPairMetadata.mapValues { it.value.build() }.toMap()),
            currencyMetadata = Collections.unmodifiableMap(currencyMetadata.mapValues { it.value.build() }.toMap()),
            warnings = Collections.unmodifiableList(debugWarnings),
        )
    }
}
