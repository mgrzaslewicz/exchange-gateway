package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.ExchangeWithCurrencyPairStringCache
import automate.profit.autocoin.exchange.currency.CurrencyPair
import mu.KLogging
import java.lang.ref.SoftReference
import java.time.Duration
import java.util.concurrent.*

interface SynchronousOrderBookFetchScheduler : OrderBookRegistrationListener {
    fun fetchOrderBooksThenNotifyListeners(exchange: SupportedExchange)
}

class DefaultSynchronousOrderBookFetchScheduler(
        private val allowedExchangeFetchFrequency: Map<SupportedExchange, Duration>,
        private val exchangeOrderBookService: ExchangeOrderBookService,
        private val orderBookListeners: OrderBookListeners,
        /** preferably one thread per exchange - cached thread pool is a good fit */
        private val scheduledExecutorService: ScheduledExecutorService,
        /** preferably a few multiple threads, but not one per single currency pair as it might grow to thousands of threads. workStealingPool might be a good fit */
        private val executorService: ExecutorService
) : SynchronousOrderBookFetchScheduler {
    companion object : KLogging()

    private val lastOrderBooks = ConcurrentHashMap<String, SoftReference<OrderBook>>()
    private val scheduledFetchers = ConcurrentHashMap<SupportedExchange, ScheduledFuture<*>>()

    override fun onListenerDeregistered(exchange: SupportedExchange, currencyPair: CurrencyPair) {
    }

    override fun onLastListenerDeregistered(exchange: SupportedExchange) {
        if (scheduledFetchers.containsKey(exchange)) {
            val scheduledFetcher = scheduledFetchers.getValue(exchange)
            scheduledFetcher.cancel(false)
            scheduledFetchers.remove(exchange)
        }
    }

    /**
     * Current synchronous fetcher implementation has to to nothing on currency pair registration as it fetches all currency pairs from exchange at one go
     */
    override fun onListenerRegistered(exchange: SupportedExchange, currencyPair: CurrencyPair) {
    }

    override fun onFirstListenerRegistered(exchange: SupportedExchange) {
        if (!scheduledFetchers.containsKey(exchange)) {
            val exchangeFrequency = allowedExchangeFetchFrequency.getValue(exchange)
            val scheduledFetcher = scheduledExecutorService.scheduleAtFixedRate({
                fetchOrderBooksThenNotifyListeners(exchange)
            }, 0, exchangeFrequency.toMillis(), TimeUnit.MILLISECONDS)
            scheduledFetchers[exchange] = scheduledFetcher
        }
    }

    override fun fetchOrderBooksThenNotifyListeners(exchange: SupportedExchange) {
        // TODO that's possibly subject to optimize and fetch all currency pairs with one exchange request where possible
        executorService.submit {
            orderBookListeners.getOrderBookListeners(exchange).forEach { (currencyPair, orderBookListeners) ->
                val orderBook = getOrderBook(exchange, currencyPair)
                if (orderBook != null && isNew(orderBook, exchange, currencyPair)) {
                    orderBookListeners.forEach {
                        it.onOrderBook(exchange, currencyPair, orderBook)
                    }
                } else {
                    orderBookListeners.forEach {
                        it.onNoNewOrderBook(exchange, currencyPair, orderBook)
                    }
                }
            }
        }
    }

    private fun getOrderBook(exchange: SupportedExchange, currencyPair: CurrencyPair): OrderBook? {
        return try {
            exchangeOrderBookService.getOrderBook(exchange.exchangeName, currencyPair)
        } catch (e: Exception) {
            logger.error { "[$exchange-$currencyPair] Error getting order book: ${e.message} (${e.stackTrace[0]})" }
            null
        }
    }

    /**
     * Using SoftReference might result in a false positive - because reference might become null when too less available memory.
     * That will lead to recalculating order book based data, so the only down side might be slightly bigger CPU usage.
     */
    private fun isNew(possiblyNewOrderBook: OrderBook, exchange: SupportedExchange, currencyPair: CurrencyPair): Boolean {
        val key = ExchangeWithCurrencyPairStringCache.get(exchange.exchangeName + currencyPair)
        val isNew = when (val lastOrderBook = lastOrderBooks[key]?.get()) {
            null -> true
            else -> !possiblyNewOrderBook.deepEquals(lastOrderBook)
        }
        if (isNew) lastOrderBooks[key] = SoftReference(possiblyNewOrderBook)
        return isNew
    }
}