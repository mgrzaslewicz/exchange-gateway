package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.currency.ExchangeWithCurrencyPairStringCache
import mu.KLogger
import mu.KotlinLogging
import java.lang.ref.SoftReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

interface SynchronousOrderBookFetchScheduler : OrderBookRegistrationListener {
    fun fetchOrderBooksThenNotifyListeners(exchange: SupportedExchange)
}

class DefaultSynchronousOrderBookFetchScheduler(
    private val exchangeOrderBookService: ExchangeOrderBookService,
    private val orderBookListeners: OrderBookListeners,
    /** Avoid using shared threads between never-ending jobs. When used fixed thread pool with the size of SupportedExchange.values().size it caused unnecessary delays and fetching was under rate limit*/
    private val executorService: Map<SupportedExchange, ExecutorService>,
    private val logger: KLogger = KotlinLogging.logger {},
    private val getOrderBookFrequentErrorLogFunction: (messageFunction: () -> String) -> Unit = { messageFunction -> logger.error(messageFunction) },
) : SynchronousOrderBookFetchScheduler {

    private val lastOrderBooks = ConcurrentHashMap<String, SoftReference<OrderBook>>()
    private val runningFetchers = ConcurrentHashMap<SupportedExchange, Future<*>>()

    override fun onListenerDeregistered(exchange: SupportedExchange, currencyPair: CurrencyPair) {
    }

    override fun onLastListenerDeregistered(exchange: SupportedExchange) {
        if (runningFetchers.containsKey(exchange)) {
            val scheduledFetcher = runningFetchers.getValue(exchange)
            scheduledFetcher.cancel(false)
            runningFetchers.remove(exchange)
        }
    }

    /**
     * Current synchronous fetcher implementation has to do nothing on currency pair registration as it fetches all currency pairs from exchange at one go
     */
    override fun onListenerRegistered(exchange: SupportedExchange, currencyPair: CurrencyPair) {
    }

    override fun onFirstListenerRegistered(exchange: SupportedExchange) {
        if (!runningFetchers.containsKey(exchange)) {
            val fetcher = executorService.getValue(exchange).submit {
                while (!Thread.currentThread().isInterrupted) {
                    fetchOrderBooksThenNotifyListeners(exchange)
                }
            }
            runningFetchers[exchange] = fetcher
        }
    }

    override fun fetchOrderBooksThenNotifyListeners(exchange: SupportedExchange) {
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

    private fun getOrderBook(exchange: SupportedExchange, currencyPair: CurrencyPair): OrderBook? {
        return try {
            exchangeOrderBookService.getOrderBook(exchange.exchangeName, currencyPair)
        } catch (e: Exception) {
            getOrderBookFrequentErrorLogFunction { "[$exchange-$currencyPair] Error getting order book: ${e.message} (${e.stackTrace[0]})" }
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
