package automate.profit.autocoin.api.exchange.metadata

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.metadata.ExchangeMetadata as SpiExchangeMetadata
import automate.profit.autocoin.spi.exchange.metadata.CurrencyPairMetadata as SpiCurrencyPairMetadata
import automate.profit.autocoin.spi.exchange.metadata.CurrencyMetadata as SpiCurrencyMetadata

data class ExchangeMetadata(
    override val exchange: ExchangeName,
    override val currencyPairMetadata: Map<out CurrencyPair, SpiCurrencyPairMetadata>,
    override val currencyMetadata: Map<String, SpiCurrencyMetadata>,
    val debugWarnings: List<String>,
) : SpiExchangeMetadata
