package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.currency.CurrencyPair
import org.knowm.xchange.Exchange
import org.knowm.xchange.dto.meta.CurrencyMetaData
import org.knowm.xchange.dto.meta.CurrencyPairMetaData


data class ExchangeMetadata(
        val supportedCurrencyPairs: List<CurrencyPair>,
        val currencies: Map<String, CurrencyMetaData>,
        val currencyPairs: Map<CurrencyPair, CurrencyPairMetaData>
) {
    companion object {
        fun fromExchange(exchange: Exchange): ExchangeMetadata {
            return ExchangeMetadata(
                    supportedCurrencyPairs = exchange.exchangeSymbols.map { CurrencyPair(it.base.currencyCode, it.counter.currencyCode) },
                    currencies = exchange.exchangeMetaData.currencies.mapKeys { it.key.currencyCode },
                    currencyPairs = exchange.exchangeMetaData.currencyPairs.mapKeys { CurrencyPair(it.key.base.currencyCode, it.key.counter.currencyCode) }
            )
        }
    }
}
