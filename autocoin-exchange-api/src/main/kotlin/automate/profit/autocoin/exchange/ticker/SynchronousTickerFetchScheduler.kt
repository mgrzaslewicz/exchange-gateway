package automate.profit.autocoin.exchange.ticker

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.currency.ExchangeWithCurrencyPairStringCache
import mu.KLogging
import java.lang.ref.SoftReference
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

interface SynchronousTickerFetchScheduler : TickerRegistrationListener {
    fun fetchTickersThenNotifyListeners(exchange: SupportedExchange)
}

class DefaultSynchronousTickerFetchScheduler(
    private val allowedExchangeFetchFrequency: Map<SupportedExchange, Duration>,
    private val exchangeTickerService: ExchangeTickerService,
    private val tickerListeners: TickerListeners,
    /** Not 100% sure if separate threads are needed here in the same way as for fetching order books.
     * However, it proved to work well there so using it here too.*/
    private val executorService: Map<SupportedExchange, ScheduledExecutorService>,
) : SynchronousTickerFetchScheduler {
    companion object : KLogging()

    private val lastTickers = mutableMapOf<String, SoftReference<Ticker>>()
    private val scheduledFetchers = ConcurrentHashMap<SupportedExchange, ScheduledFuture<*>>()

    override fun onLastListenerDeregistered(exchange: SupportedExchange) {
        if (scheduledFetchers.containsKey(exchange)) {
            val scheduledFetcher = scheduledFetchers.getValue(exchange)
            scheduledFetcher.cancel(false)
            scheduledFetchers.remove(exchange)
        }
    }

    override fun onListenerDeregistered(exchange: SupportedExchange, currencyPair: CurrencyPair) {
    }

    override fun onFirstListenerRegistered(exchange: SupportedExchange) {
        if (!scheduledFetchers.containsKey(exchange)) {
            val exchangeFrequency = allowedExchangeFetchFrequency.getValue(exchange)
            val scheduledFetcher = executorService.getValue(exchange).scheduleAtFixedRate({
                fetchTickersThenNotifyListeners(exchange)
            }, 0, exchangeFrequency.toMillis(), TimeUnit.MILLISECONDS)
            scheduledFetchers[exchange] = scheduledFetcher
        }
    }

    override fun onListenerRegistered(exchange: SupportedExchange, currencyPair: CurrencyPair) {
    }

    override fun fetchTickersThenNotifyListeners(exchange: SupportedExchange) {
        // TODO that's possibly subject to optimize and fetch all currency pairs with one exchange request where possible
        executorService.getValue(exchange).submit {
            tickerListeners.getTickerListeners(exchange).forEach { (currencyPair, tickerListeners) ->
                val ticker = getTicker(exchange, currencyPair)
                if (ticker != null && isNew(ticker, exchange, currencyPair)) {
                    tickerListeners.forEach {
                        it.onTicker(exchange, currencyPair, ticker)
                    }
                } else {
                    tickerListeners.forEach {
                        it.onNoNewTicker(exchange, currencyPair, ticker)
                    }
                }
            }
        }
    }

    private fun getTicker(exchange: SupportedExchange, currencyPair: CurrencyPair): Ticker? {
        return try {
            exchangeTickerService.getTicker(exchange.exchangeName, currencyPair)
        } catch (e: Exception) {
            logger.error { "[$exchange-$currencyPair] Error getting ticker: ${e.message} (${e.stackTrace[0]})" }
            null
        }
    }

    private fun isNew(possiblyNewTicker: Ticker, exchange: SupportedExchange, currencyPair: CurrencyPair): Boolean {
        val key = ExchangeWithCurrencyPairStringCache.get(exchange.exchangeName + currencyPair)
        val isNew = when (val lastTicker = lastTickers[key]?.get()) {
            null -> true
            else -> possiblyNewTicker != lastTicker
        }
        if (isNew) lastTickers[key] = SoftReference(possiblyNewTicker)
        return isNew
    }
}
