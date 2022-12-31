package automate.profit.autocoin.exchange.ticker

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.cache.ExchangeWithCurrencyPairStringCache
import automate.profit.autocoin.exchange.currency.CurrencyPair
import mu.KLogging
import java.time.Duration
import java.util.concurrent.*

interface SynchronousTickerFetchScheduler : TickerRegistrationListener {
    fun fetchTickersThenNotifyListeners(exchange: SupportedExchange)
}

class DefaultSynchronousTickerFetchScheduler(
        private val allowedExchangeFetchFrequency: Map<SupportedExchange, Duration>,
        private val exchangeTickerService: ExchangeTickerService,
        private val tickerListeners: TickerListeners,
        /** preferably one thread per exchange - cached thread pool is a good fit */
        private val scheduledExecutorService: ScheduledExecutorService,
        /** preferably a few multiple threads, but not one per single currency pair as it might grow to thousands of threads. workStealingPool might be a good fit */
        private val executorService: ExecutorService
) : SynchronousTickerFetchScheduler {
    companion object : KLogging()

    private val lastTicker = mutableMapOf<String, Ticker>()
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
            val scheduledFetcher = scheduledExecutorService.scheduleAtFixedRate({
                fetchTickersThenNotifyListeners(exchange)
            }, 0, exchangeFrequency.toMillis(), TimeUnit.MILLISECONDS)
            scheduledFetchers[exchange] = scheduledFetcher
        }
    }

    override fun onListenerRegistered(exchange: SupportedExchange, currencyPair: CurrencyPair) {
    }

    override fun fetchTickersThenNotifyListeners(exchange: SupportedExchange) {
        // TODO that's possibly subject to optimize and fetch all currency pairs with one exchange request where possible
        executorService.submit {
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
        val isNew = when (val lastTicker = lastTicker[key]) {
            null -> true
            else -> possiblyNewTicker != lastTicker
        }
        if (isNew) this.lastTicker[key] = possiblyNewTicker
        return isNew
    }
}