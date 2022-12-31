package com.autocoin.exchangegateway.api.exchange.ticker.service.authorized

import com.autocoin.exchangegateway.api.exchange.ticker.XchangeTickerTransformer
import com.autocoin.exchangegateway.api.exchange.ticker.XchangeTickerTransformerWithCurrencyPair
import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.ticker.Ticker
import com.autocoin.exchangegateway.spi.exchange.ticker.gateway.InvalidCurrencyPairException
import com.autocoin.exchangegateway.spi.exchange.ticker.service.authorized.AuthorizedTickerService
import org.knowm.xchange.exceptions.CurrencyPairNotValidException
import org.knowm.xchange.service.marketdata.MarketDataService
import org.knowm.xchange.service.marketdata.params.CurrencyPairsParam
import java.time.Clock
import java.util.function.Function
import org.knowm.xchange.currency.CurrencyPair as XchangeCurrencyPair

class XchangeAuthorizedTickerService<T>(
    override val exchangeName: ExchangeName,
    override val apiKey: ApiKeySupplier<T>,
    val delegate: MarketDataService,
    private val currencyPairToXchange: Function<CurrencyPair, XchangeCurrencyPair>,
    private val currencyPairsToXchangeCurrencyPairsParam: Function<Collection<CurrencyPair>, CurrencyPairsParam>,
    private val xchangeTickerTransformerWithCurrencyPair: XchangeTickerTransformerWithCurrencyPair,
    private val xchangeTickerTransformer: XchangeTickerTransformer,
    private val clock: Clock,
) : AuthorizedTickerService<T> {

    override fun getTicker(currencyPair: CurrencyPair): Ticker {
        return try {
            val xchangeCurrencyPair = currencyPairToXchange.apply(currencyPair)
            delegate.getTicker(xchangeCurrencyPair).let {
                xchangeTickerTransformerWithCurrencyPair(
                    exchangeName = exchangeName,
                    currencyPair = currencyPair,
                    xchangeTicker = it,
                    receivedAtMillis = clock.millis(),
                )
            }
        } catch (e: CurrencyPairNotValidException) {
            throw InvalidCurrencyPairException(currencyPair)
        }
    }

    override fun getTickers(currencyPairs: Collection<CurrencyPair>): List<Ticker> {
        val currencyPairsParam = currencyPairsToXchangeCurrencyPairsParam.apply(currencyPairs)
        return delegate.getTickers(currencyPairsParam)
            .map {
                xchangeTickerTransformer(
                    exchangeName = exchangeName,
                    xchangeTicker = it,
                    receivedAtMillis = clock.millis(),
                )
            }
    }

}
