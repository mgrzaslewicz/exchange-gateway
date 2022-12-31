package automate.profit.autocoin.exchange.currency

import java.util.function.Function
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair as SpiCurrencyPair
import org.knowm.xchange.currency.CurrencyPair as XchangeCurrencyPair

val defaultXchangeCurrencyPairTransformer: Function<XchangeCurrencyPair, SpiCurrencyPair> = Function {
    automate.profit.autocoin.api.exchange.currency.CurrencyPair.of(it.base.currencyCode, it.counter.currencyCode)
}

val defaultCurrencyPairToXchange: Function<SpiCurrencyPair, XchangeCurrencyPair> = Function {
    XchangeCurrencyPair(it.base, it.counter)
}
