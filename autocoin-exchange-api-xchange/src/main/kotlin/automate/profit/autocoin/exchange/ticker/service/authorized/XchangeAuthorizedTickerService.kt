package automate.profit.autocoin.exchange.ticker.service.authorized

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.ticker.Ticker
import automate.profit.autocoin.spi.exchange.ticker.gateway.InvalidCurrencyPairException
import automate.profit.autocoin.spi.exchange.ticker.service.authorized.AuthorizedTickerService
import automate.profit.autocoin.exchange.ticker.XchangeTickerTransformer
import automate.profit.autocoin.exchange.ticker.XchangeTickerTransformerWithCurrencyPair
import org.knowm.xchange.exceptions.CurrencyPairNotValidException
import org.knowm.xchange.service.marketdata.MarketDataService
import org.knowm.xchange.service.marketdata.params.CurrencyPairsParam
import java.time.Clock
import java.util.function.Function
import org.knowm.xchange.currency.CurrencyPair as XchangeCurrencyPair

class XchangeAuthorizedTickerService(
    override val exchangeName: ExchangeName,
    val delegate: MarketDataService,
    private val currencyPairToXchange: Function<CurrencyPair, XchangeCurrencyPair>,
    private val currencyPairsToXchangeCurrencyPairsParam: Function<Collection<CurrencyPair>, CurrencyPairsParam>,
    private val xchangeTickerTransformerWithCurrencyPair: XchangeTickerTransformerWithCurrencyPair,
    private val xchangeTickerTransformer: XchangeTickerTransformer,
    private val clock: Clock,
) : AuthorizedTickerService {

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
