package com.autocoin.exchangegateway.api.exchange.currency

import java.util.function.Function
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair as SpiCurrencyPair
import org.knowm.xchange.currency.CurrencyPair as XchangeCurrencyPair

val defaultXchangeCurrencyPairTransformer: Function<XchangeCurrencyPair, SpiCurrencyPair> = Function {
    CurrencyPair.of(it.base.currencyCode, it.counter.currencyCode)
}

val defaultCurrencyPairToXchange: Function<SpiCurrencyPair, XchangeCurrencyPair> = Function {
    XchangeCurrencyPair(it.base, it.counter)
}
