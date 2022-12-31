package automate.profit.autocoin.exchange.ticker.service.authorized

import automate.profit.autocoin.api.exchange.ticker.Ticker
import automate.profit.autocoin.exchange.currency.defaultCurrencyPairToXchange
import automate.profit.autocoin.exchange.currency.defaultXchangeCurrencyPairTransformer
import automate.profit.autocoin.exchange.ticker.XchangeTickerTransformer
import automate.profit.autocoin.exchange.ticker.XchangeTickerTransformerWithCurrencyPair
import automate.profit.autocoin.exchange.xchange.XchangeProvider
import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier
import automate.profit.autocoin.spi.exchange.ticker.service.authorized.AuthorizedTickerService
import automate.profit.autocoin.spi.exchange.ticker.service.authorized.AuthorizedTickerServiceFactory
import org.knowm.xchange.service.marketdata.params.CurrencyPairsParam
import java.time.Clock
import java.util.function.Function
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair as SpiCurrencyPair
import automate.profit.autocoin.spi.exchange.ticker.Ticker as SpiTicker
import org.knowm.xchange.currency.CurrencyPair as XchangeCurrencyPair
import org.knowm.xchange.dto.marketdata.Ticker as XchangeTicker


class XchangeAuthorizedTickerServiceFactory<T>(
    private val xchangeProvider: XchangeProvider<T>,
    private val currencyPairToXchange: Function<SpiCurrencyPair, XchangeCurrencyPair> = defaultCurrencyPairToXchange,
    private val xchangeCurrencyPairTransformer: Function<XchangeCurrencyPair, SpiCurrencyPair> = defaultXchangeCurrencyPairTransformer,
    private val xchangeTickerTransformerWithCurrencyPair: XchangeTickerTransformerWithCurrencyPair = defaultXchangeTickerTransformerWithCurrencyPairFix,
    private val xchangeTickerTransformer: XchangeTickerTransformer = defaultXchangeTickerTransformer(xchangeCurrencyPairTransformer),
    private val currencyPairsToXchangeCurrencyPairsParam: Function<Collection<SpiCurrencyPair>, CurrencyPairsParam> = currencyPairsToXchangeCurrencyPairsParam(currencyPairToXchange),
    private val clock: Clock,
) : AuthorizedTickerServiceFactory<T> {
    companion object {

        val defaultXchangeTickerTransformerWithCurrencyPairFix: XchangeTickerTransformerWithCurrencyPair = object : XchangeTickerTransformerWithCurrencyPair {
            override operator fun invoke(
                exchangeName: ExchangeName,
                currencyPair: SpiCurrencyPair,
                xchangeTicker: XchangeTicker,
                receivedAtMillis: Long,
            ): SpiTicker {
                return Ticker(
                    exchangeName = exchangeName,
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
                    exchangeName: ExchangeName,
                    xchangeTicker: XchangeTicker,
                    receivedAtMillis: Long,
                ): SpiTicker {
                    return Ticker(
                        exchangeName = exchangeName,
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
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedTickerService<T> {
        val xchange = xchangeProvider(exchangeName = exchangeName, apiKey = apiKey)
        return XchangeAuthorizedTickerService(
            exchangeName = exchangeName,
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
