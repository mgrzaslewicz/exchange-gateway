package com.autocoin.exchangegateway.api.exchange.ticker.service.authorized

import com.autocoin.exchangegateway.api.exchange.ticker.Ticker
import com.autocoin.exchangegateway.api.exchange.ticker.XchangeTickerTransformer
import com.autocoin.exchangegateway.api.exchange.ticker.XchangeTickerTransformerWithCurrencyPair
import com.autocoin.exchangegateway.api.exchange.xchange.XchangeProvider
import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.ticker.service.authorized.AuthorizedTickerService
import com.autocoin.exchangegateway.spi.exchange.ticker.service.authorized.AuthorizedTickerServiceFactory
import org.knowm.xchange.service.marketdata.params.CurrencyPairsParam
import java.time.Clock
import java.util.function.Function
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair as SpiCurrencyPair
import com.autocoin.exchangegateway.spi.exchange.ticker.Ticker as SpiTicker
import org.knowm.xchange.currency.CurrencyPair as XchangeCurrencyPair
import org.knowm.xchange.dto.marketdata.Ticker as XchangeTicker


class XchangeAuthorizedTickerServiceFactory<T>(
    private val xchangeProvider: XchangeProvider<T>,
    private val currencyPairToXchange: Function<SpiCurrencyPair, XchangeCurrencyPair> = com.autocoin.exchangegateway.api.exchange.currency.defaultCurrencyPairToXchange,
    private val xchangeCurrencyPairTransformer: Function<XchangeCurrencyPair, SpiCurrencyPair> = com.autocoin.exchangegateway.api.exchange.currency.defaultXchangeCurrencyPairTransformer,
    private val xchangeTickerTransformerWithCurrencyPair: XchangeTickerTransformerWithCurrencyPair = defaultXchangeTickerTransformerWithCurrencyPairFix,
    private val xchangeTickerTransformer: XchangeTickerTransformer = defaultXchangeTickerTransformer(xchangeCurrencyPairTransformer),
    private val currencyPairsToXchangeCurrencyPairsParam: Function<Collection<SpiCurrencyPair>, CurrencyPairsParam> = currencyPairsToXchangeCurrencyPairsParam(currencyPairToXchange),
    private val clock: Clock,
) : AuthorizedTickerServiceFactory<T> {
    companion object {

        val defaultXchangeTickerTransformerWithCurrencyPairFix: XchangeTickerTransformerWithCurrencyPair = object : XchangeTickerTransformerWithCurrencyPair {
            override operator fun invoke(
                exchange: Exchange,
                currencyPair: SpiCurrencyPair,
                xchangeTicker: XchangeTicker,
                receivedAtMillis: Long,
            ): SpiTicker {
                return Ticker(
                    exchange = exchange,
                    currencyPair = currencyPair,
                    ask = xchangeTicker.ask,
                    bid = xchangeTicker.bid,
                    baseCurrency24hVolume = xchangeTicker.volume,
                    counterCurrency24hVolume = xchangeTicker.quoteVolume,
                    receivedAtMillis = receivedAtMillis,
                    exchangeTimestampMillis = xchangeTicker.timestamp?.time,
                )
            }
        }

        fun currencyPairsToXchangeCurrencyPairsParam(currencyPairToXchange: Function<SpiCurrencyPair, XchangeCurrencyPair>): Function<Collection<SpiCurrencyPair>, CurrencyPairsParam> =
            Function {
                CurrencyPairsParam {
                    it.map { currencyPairToXchange.apply(it) }
                }
            }

        fun defaultXchangeTickerTransformer(xchangeCurrencyPairTransformer: Function<XchangeCurrencyPair, SpiCurrencyPair>): XchangeTickerTransformer =
            object : XchangeTickerTransformer {
                override operator fun invoke(
                    exchange: Exchange,
                    xchangeTicker: XchangeTicker,
                    receivedAtMillis: Long,
                ): SpiTicker {
                    return Ticker(
                        exchange = exchange,
                        currencyPair = xchangeCurrencyPairTransformer.apply(xchangeTicker.currencyPair),
                        ask = xchangeTicker.ask,
                        bid = xchangeTicker.bid,
                        baseCurrency24hVolume = xchangeTicker.volume,
                        counterCurrency24hVolume = xchangeTicker.quoteVolume,
                        receivedAtMillis = receivedAtMillis,
                        exchangeTimestampMillis = xchangeTicker.timestamp?.time,
                    )
                }
            }
    }

    override fun createAuthorizedTickerService(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedTickerService<T> {
        val xchange = xchangeProvider(
            exchange = exchange,
            apiKey = apiKey,
        )
        return XchangeAuthorizedTickerService(
            exchange = exchange,
            apiKey = apiKey,
            delegate = xchange.marketDataService,
            currencyPairToXchange = currencyPairToXchange,
            xchangeTickerTransformerWithCurrencyPair = xchangeTickerTransformerWithCurrencyPair,
            currencyPairsToXchangeCurrencyPairsParam = currencyPairsToXchangeCurrencyPairsParam,
            xchangeTickerTransformer = xchangeTickerTransformer,
            clock = clock,
        )
    }

}
