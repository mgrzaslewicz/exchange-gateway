package com.autocoin.exchangegateway.api.exchange.ticker

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.ticker.Ticker as SpiTicker
import org.knowm.xchange.dto.marketdata.Ticker as XchangeTicker

/**
 * Override currency pair as a fix because it sometimes happens that currency pair is wrongly provided
 * from underlying xchange market data implementations.
 * eg COINBENE has hardcoded substring (0, 3) so it parses improperly currencies with
 * length > 3 and STORJ/BTC fetched from exchange becomes STO/RJBTC
 */
interface XchangeTickerTransformerWithCurrencyPair {
    operator fun invoke(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        xchangeTicker: XchangeTicker,
        receivedAtMillis: Long,
    ): SpiTicker
}

interface XchangeTickerTransformer {
    operator fun invoke(
        exchange: Exchange,
        xchangeTicker: XchangeTicker,
        receivedAtMillis: Long,
    ): SpiTicker
}
