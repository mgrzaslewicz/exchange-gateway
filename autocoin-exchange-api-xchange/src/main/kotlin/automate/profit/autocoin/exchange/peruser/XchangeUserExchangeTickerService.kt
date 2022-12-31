package automate.profit.autocoin.exchange.peruser

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.currency.toXchangeCurrencyPair
import automate.profit.autocoin.exchange.ticker.InvalidCurrencyPairException
import automate.profit.autocoin.exchange.ticker.Ticker
import automate.profit.autocoin.exchange.ticker.UserExchangeTickerService
import automate.profit.autocoin.ticker.toTickerWithCurrencyPairFix
import mu.KLogging
import org.knowm.xchange.exceptions.CurrencyPairNotValidException
import org.knowm.xchange.service.marketdata.MarketDataService


class XchangeUserExchangeTickerService(
        private val marketDataService: MarketDataService,
        private val exchange: SupportedExchange) : UserExchangeTickerService {
    companion object : KLogging()

    private var isFirstInvalidCurrencyPairLogged = false

    override fun getTicker(currencyPair: CurrencyPair): Ticker {
        return try {
            var xchangeTicker = marketDataService.getTicker(currencyPair.toXchangeCurrencyPair())
            if (currencyPair.base != xchangeTicker.currencyPair.base.currencyCode || currencyPair.counter != xchangeTicker.currencyPair.counter.currencyCode) {
                logger.error { "[$exchange-$currencyPair] Xchange implementation provided invalid currencyPair. expected=$currencyPair, actual=${xchangeTicker.currencyPair}" }
                isFirstInvalidCurrencyPairLogged = true
            }
            return xchangeTicker.toTickerWithCurrencyPairFix(currencyPair)
        } catch (e: CurrencyPairNotValidException) {
            throw InvalidCurrencyPairException(currencyPair)
        }
    }
}

