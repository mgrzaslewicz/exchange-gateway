package automate.profit.autocoin.exchange.peruser

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.currency.toXchangeCurrencyPair
import automate.profit.autocoin.exchange.ticker.InvalidCurrencyPairException
import automate.profit.autocoin.exchange.ticker.Ticker
import automate.profit.autocoin.exchange.ticker.UserExchangeTickerService
import automate.profit.autocoin.ticker.toTicker
import org.knowm.xchange.exceptions.CurrencyPairNotValidException
import org.knowm.xchange.service.marketdata.MarketDataService


class XchangeUserExchangeTickerService(private val marketDataService: MarketDataService) : UserExchangeTickerService {
    override fun getTicker(currencyPair: CurrencyPair): Ticker {
        return try {
            marketDataService.getTicker(currencyPair.toXchangeCurrencyPair()).toTicker()
        } catch (e: CurrencyPairNotValidException) {
            throw InvalidCurrencyPairException(currencyPair)
        }
    }
}

