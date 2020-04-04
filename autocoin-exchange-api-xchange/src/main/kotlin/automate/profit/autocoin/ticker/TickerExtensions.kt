package automate.profit.autocoin.ticker

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.currency.toXchangeCurrencyPair
import automate.profit.autocoin.exchange.ticker.Ticker
import java.util.*
import org.knowm.xchange.dto.marketdata.Ticker as XchangeTicker


fun Ticker.toXchangeTicker(): XchangeTicker = XchangeTicker.Builder()
        .currencyPair(currencyPair.toXchangeCurrencyPair())
        .ask(ask)
        .bid(bid)
        .volume(baseCurrency24hVolume)
        .quoteVolume(counterCurrency24hVolume)
        .timestamp(timestamp?.toEpochMilli()?.let { Date(it) })
        .build()

fun XchangeTicker.toTicker() = Ticker(
        currencyPair = CurrencyPair.of(
                base = currencyPair.base.currencyCode,
                counter = currencyPair.counter.currencyCode
        ),
        ask = ask,
        bid = bid,
        baseCurrency24hVolume = volume,
        counterCurrency24hVolume = quoteVolume,
        timestamp = this.timestamp?.toInstant()
)

/**
 * Override currency pair as a fix because it sometimes happens that currency pair is wrongly provided
 * from underlying xchange market data implementations.
 * eg COINBENE has hardcoded substring (0, 3) so it parses improperly currencies with
 * length > 3 and STORJ/BTC fetched from exchange becomes STO/RJBTC
 */
fun XchangeTicker.toTickerWithCurrencyPairFix(currencyPair: CurrencyPair) = Ticker(
        currencyPair = currencyPair,
        ask = ask,
        bid = bid,
        baseCurrency24hVolume = volume,
        counterCurrency24hVolume = quoteVolume,
        timestamp = this.timestamp?.toInstant()
)

