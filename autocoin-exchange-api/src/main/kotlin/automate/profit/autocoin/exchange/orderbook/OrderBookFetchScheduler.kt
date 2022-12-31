package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.order.ExchangeOrderBookService
import mu.KLogging
import java.time.Duration
import java.util.concurrent.*

interface OrderBookFetchScheduler : OrderBookRegistrationListener, OrderBookListenersVisitor {
}

class DefaultOrderBookFetchScheduler(
        private val allowedExchangeFetchFrequency: Map<SupportedExchange, Duration>,
        private val exchangeOrderBookService: ExchangeOrderBookService,
        private val orderBookListeners: OrderBookListeners,
        /** preferably one thread per exchange - cached thread pool is a good fit */
        private val scheduledExecutorService: ScheduledExecutorService,
        /** preferably a few multiple threads, but not one per single currency pair as it might grow to thousands of threads. workStealingPool might be a good fit */
        private val executorService: ExecutorService
) : OrderBookFetchScheduler {
    companion object : KLogging()

    private val lastOrderBook = mutableMapOf<String, OrderBook>()
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
                orderBookListeners.iterateOverEachExchangeAndAllCurrencyPairs(this)
            }, 0, exchangeFrequency.toMillis(), TimeUnit.MILLISECONDS)
            scheduledFetchers[exchange] = scheduledFetcher
        }
    }

    override fun fetchOrderBooksThenNotifyListeners(exchange: SupportedExchange, currencyPairsWithListeners: Map<CurrencyPair, Set<OrderBookListener>>) {
        // TODO that's possibly subject to optimize and fetch all currency pairs with one exchange request where possible
        executorService.submit {
            currencyPairsWithListeners.forEach { (currencyPair, orderBookListeners) ->
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
            logger.error { "[$exchange-$currencyPair]Error getting order book: ${e.message} (${e.stackTrace[0]})" }
            null
        }
    }

    private fun isNew(possiblyNewOrderBook: OrderBook, exchange: SupportedExchange, currencyPair: CurrencyPair): Boolean {
        val key = exchange.exchangeName + currencyPair
        val isNew = when (val lastOrderBook = lastOrderBook[key]) {
            null -> true
            else -> possiblyNewOrderBook != lastOrderBook
        }
        if (isNew) this.lastOrderBook[key] = possiblyNewOrderBook
        return isNew
    }
}