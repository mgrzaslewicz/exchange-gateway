package automate.profit.autocoin.exchange.ticker

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import mu.KLogging
import java.time.Duration
import java.util.concurrent.*

interface TickerFetchScheduler : TickerRegistrationListener, TickerListenersVisitor {
}

class DefaultTickerFetchScheduler(
        private val allowedExchangeFetchFrequency: Map<SupportedExchange, Duration>,
        private val exchangeTickerService: ExchangeTickerService,
        private val tickerListeners: TickerListeners,
        /** preferably one thread per exchange - cached thread pool is a good fit */
        private val scheduledExecutorService: ScheduledExecutorService,
        /** preferably a few multiple threads, but not one per single currency pair as it might grow to thousands of threads. workStealingPool might be a good fit */
        private val executorService: ExecutorService
) : TickerFetchScheduler {
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

    override fun onFirstListenerRegistered(exchange: SupportedExchange) {
        if (!scheduledFetchers.containsKey(exchange)) {
            val exchangeFrequency = allowedExchangeFetchFrequency.getValue(exchange)
            val scheduledFetcher = scheduledExecutorService.scheduleAtFixedRate({
                tickerListeners.iterateOverEachExchangeAndAllCurrencyPairs(this)
            }, 0, exchangeFrequency.toMillis(), TimeUnit.MILLISECONDS)
            scheduledFetchers[exchange] = scheduledFetcher
        }
    }

    override fun fetchTickersThenNotifyListeners(exchange: SupportedExchange, currencyPairsWithListeners: Map<CurrencyPair, Set<TickerListener>>) {
        // TODO that's possibly subject to optimize and fetch all currency pairs with one exchange request where possible
        executorService.submit {
            currencyPairsWithListeners.forEach { (currencyPair, orderBookListeners) ->
                val orderBook = getTicker(exchange, currencyPair)
                if (orderBook != null && isNew(orderBook, exchange, currencyPair)) {
                    orderBookListeners.forEach {
                        it.onTicker(exchange, currencyPair, orderBook)
                    }
                } else {
                    orderBookListeners.forEach {
                        it.onNoNewTicker(exchange, currencyPair, orderBook)
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
        val key = exchange.exchangeName + currencyPair
        val isNew = when (val lastTicker = lastTicker[key]) {
            null -> true
            else -> possiblyNewTicker != lastTicker
        }
        if (isNew) this.lastTicker[key] = possiblyNewTicker
        return isNew
    }
}