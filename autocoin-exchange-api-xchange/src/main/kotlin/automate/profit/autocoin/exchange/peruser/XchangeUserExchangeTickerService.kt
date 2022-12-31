package automate.profit.autocoin.exchange.peruser

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.currency.toXchangeCurrencyPair
import automate.profit.autocoin.exchange.ticker.InvalidCurrencyPairException
import automate.profit.autocoin.exchange.ticker.Ticker
import automate.profit.autocoin.exchange.ticker.UserExchangeTickerService
import automate.profit.autocoin.ticker.toTicker
import automate.profit.autocoin.ticker.toTickerWithCurrencyPairFix
import mu.KLogging
import org.knowm.xchange.exceptions.CurrencyPairNotValidException
import org.knowm.xchange.service.marketdata.MarketDataService
import org.knowm.xchange.service.marketdata.params.CurrencyPairsParam


class XchangeUserExchangeTickerService(
        private val marketDataService: MarketDataService,
        private val exchange: SupportedExchange) : UserExchangeTickerService {
    companion object : KLogging()

    private var isFirstInvalidCurrencyPairLogged = false

    override fun getTicker(currencyPair: CurrencyPair): Ticker {
        return try {
            val xchangeTicker = marketDataService.getTicker(currencyPair.toXchangeCurrencyPair())
            logInvalidCurrencyPair(currencyPair, xchangeTicker)
            return xchangeTicker.toTickerWithCurrencyPairFix(currencyPair)
        } catch (e: CurrencyPairNotValidException) {
            throw InvalidCurrencyPairException(currencyPair)
        }
    }

    private fun logInvalidCurrencyPair(currencyPair: CurrencyPair, xchangeTicker: org.knowm.xchange.dto.marketdata.Ticker) {
        if (currencyPair.base != xchangeTicker.currencyPair.base.currencyCode || currencyPair.counter != xchangeTicker.currencyPair.counter.currencyCode) {
            logger.error { "[$exchange-$currencyPair] Xchange implementation provided invalid currencyPair. expected=$currencyPair, actual=${xchangeTicker.currencyPair}" }
            isFirstInvalidCurrencyPairLogged = true
        }
    }

    override fun getTickers(currencyPairs: Collection<CurrencyPair>): List<Ticker> {
        val xchangeTickers = marketDataService.getTickers(CurrencyPairsParam {
            currencyPairs.map { it.toXchangeCurrencyPair() }
        })
        return xchangeTickers
                .map { it.toTicker() }
                .filter { currencyPairs.contains(it.currencyPair) }
    }
}

