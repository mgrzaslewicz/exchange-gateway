package automate.profit.autocoin.ticker

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.currency.toXchangeCurrencyPair
import automate.profit.autocoin.exchange.ticker.Ticker
import java.util.*
import org.knowm.xchange.dto.marketdata.Ticker as XchangeTicker


fun Ticker.toXchangeTicker(): XchangeTicker = XchangeTicker.Builder()
        .currencyPair(this.currencyPair.toXchangeCurrencyPair())
        .ask(this.ask)
        .bid(this.bid)
        .timestamp(this.timestamp?.toEpochMilli()?.let { Date(it) })
        .build()

fun XchangeTicker.toTicker() = Ticker(
        currencyPair = CurrencyPair(base = this.currencyPair.base.currencyCode, counter = this.currencyPair.counter.currencyCode),
        ask = this.ask,
        bid = this.bid,
        timestamp = this.timestamp?.toInstant()
)

