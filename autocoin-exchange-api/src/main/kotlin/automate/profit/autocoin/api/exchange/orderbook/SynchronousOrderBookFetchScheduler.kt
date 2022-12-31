package automate.profit.autocoin.api.exchange.orderbook

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.currency.ExchangeWithCurrencyPairStringCache
import automate.profit.autocoin.spi.exchange.orderbook.gateway.OrderBookServiceGateway
import automate.profit.autocoin.spi.exchange.orderbook.listener.OrderBookListeners
import automate.profit.autocoin.spi.exchange.orderbook.listener.OrderBookRegistrationListener
import mu.KLogger
import mu.KotlinLogging
import java.lang.ref.SoftReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import automate.profit.autocoin.spi.exchange.orderbook.OrderBook as SpiOrderBook

interface SynchronousOrderBookFetchScheduler : OrderBookRegistrationListener {
    fun fetchOrderBooksThenNotifyListeners(exchangeName: ExchangeName)
}

class DefaultSynchronousOrderBookFetchScheduler(
    private val orderBookServiceGateway: OrderBookServiceGateway,
    private val orderBookListeners: OrderBookListeners,
    /** Avoid using shared threads between never-ending jobs. When used fixed thread pool with the size of SupportedExchange.values().size it caused unnecessary delays and fetching was under rate limit*/
    private val executorService: Map<ExchangeName, ExecutorService>,
    private val logger: KLogger = KotlinLogging.logger {},
    private val getOrderBookFrequentErrorLogFunction: (messageFunction: () -> String) -> Unit = { messageFunction -> logger.error(messageFunction) },
) : SynchronousOrderBookFetchScheduler {

    private val lastOrderBooks = ConcurrentHashMap<String, SoftReference<SpiOrderBook>>()
    private val runningFetchers = ConcurrentHashMap<ExchangeName, Future<*>>()

    override fun onListenerDeregistered(
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
    ) {
    }

    override fun onLastListenerDeregistered(exchangeName: ExchangeName) {
        if (runningFetchers.containsKey(exchangeName)) {
            val scheduledFetcher = runningFetchers.getValue(exchangeName)
            scheduledFetcher.cancel(false)
            runningFetchers.remove(exchangeName)
        }
    }

    /**
     * Current synchronous fetcher implementation has to do nothing on currency pair registration as it fetches all currency pairs from exchange at one go
     */
    override fun onListenerRegistered(
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
    ) {
    }

    override fun onFirstListenerRegistered(exchangeName: ExchangeName) {
        if (!runningFetchers.containsKey(exchangeName)) {
            val fetcher = executorService.getValue(exchangeName).submit {
                while (!Thread.currentThread().isInterrupted) {
                    fetchOrderBooksThenNotifyListeners(exchangeName)
                }
            }
            runningFetchers[exchangeName] = fetcher
        }
    }

    override fun fetchOrderBooksThenNotifyListeners(exchangeName: ExchangeName) {
        orderBookListeners.getOrderBookListeners(exchangeName).forEach { (currencyPair, orderBookListeners) ->
            val orderBook = getOrderBook(exchangeName, currencyPair)
            if (orderBook != null && isNew(orderBook, exchangeName, currencyPair)) {
                orderBookListeners.forEach {
                    it.onOrderBook(exchangeName, currencyPair, orderBook)
                }
            }
            else {
                orderBookListeners.forEach {
                    it.onNoNewOrderBook(exchangeName, currencyPair, orderBook)
                }
            }
        }
    }

    private fun getOrderBook(
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
    ): SpiOrderBook? {
        return try {
            orderBookServiceGateway.getOrderBook(exchangeName, currencyPair)
        } catch (e: Exception) {
            getOrderBookFrequentErrorLogFunction { "[$exchangeName-$currencyPair] Error getting order book: ${e.message} (${e.stackTrace[0]})" }
            null
        }
    }

    /**
     * Using SoftReference might result in a false positive - because reference might become null when too less available memory.
     * That will lead to recalculating order book based data, so the only down side might be slightly bigger CPU usage.
     */
    private fun isNew(
        possiblyNewOrderBook: SpiOrderBook,
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
    ): Boolean {
        val key = ExchangeWithCurrencyPairStringCache.get(exchangeName.value + currencyPair)
        val isNew = when (val lastOrderBook = lastOrderBooks[key]?.get()) {
            null -> true
            else -> !possiblyNewOrderBook.deepEquals(lastOrderBook)
        }
        if (isNew) lastOrderBooks[key] = SoftReference(possiblyNewOrderBook)
        return isNew
    }
}
