package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.order.UserExchangeOrderBookService
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

interface OrderBookListenerRegistrar {
    fun registerOrderBookListener(orderBookListener: OrderBookListener): Boolean
    fun fetchOrderBooksAndNotifyListeners()
    fun removeOrderBookListener(orderBookListener: OrderBookListener): Boolean
    fun <T : OrderBookListener> getListenersOfClass(`class`: Class<T>): Map<CurrencyPair, Set<T>>
    val exchangeName: SupportedExchange
}


class DefaultOrderBookListenerRegistrar(override val exchangeName: SupportedExchange, private val userExchagneOrderBookService
: UserExchangeOrderBookService) : OrderBookListenerRegistrar {

    private val logger = KotlinLogging.logger("${DefaultOrderBookListenerRegistrar::class.java.name}.$exchangeName")

    private val listeners = mutableMapOf<CurrencyPair, MutableSet<OrderBookListener>>()

    private val lastOrderBook = mutableMapOf<CurrencyPair, OrderBook>()

    override fun registerOrderBookListener(orderBookListener: OrderBookListener): Boolean {
        val currencyPair = orderBookListener.currencyPair()
        logger.info("Registering listener for $exchangeName-$currencyPair")
        return if (!listeners.contains(currencyPair)) {
            listeners[currencyPair] = mutableSetOf(orderBookListener)
            logger.info("Registered first listener for $exchangeName-$currencyPair")
            true
        } else if (listeners[currencyPair]!!.add(orderBookListener)) {
            logger.info("Registered another listener for $exchangeName-$currencyPair")
            true
        } else {
            logger.warn("Registered another listener for $exchangeName-$currencyPair")
            false
        }
    }

    override fun fetchOrderBooksAndNotifyListeners() {
        runBlocking {
            val jobs = mutableListOf<Job>()
            listeners.keys.forEach { currencyPair ->
                jobs += launch { fetchOrderBookAndNotifyListeners(currencyPair) }
            }
            logger.info("Waiting for all currencies at $exchangeName...")
            jobs.forEach { it.join() }
        }
        logger.info("All currencies done at $exchangeName.")
    }

    private fun fetchOrderBookAndNotifyListeners(currencyPair: CurrencyPair) {
        val orderBook = getOrderBookForCurrencyPair(currencyPair)
        if (orderBook != null) {
            if (isNew(orderBook, currencyPair)) {
                logger.info { "New order book at $exchangeName: $orderBook" }
                notifyAllCurrencyPairListeners(currencyPair, orderBook)
            } else {
                logger.info("No new order book at $exchangeName for currency pair $currencyPair}")
                notifyAllListenersNoNewOrderBook(currencyPair, orderBook)
            }
        } else {
            logger.info("No ticker at $exchangeName for currency pair $currencyPair")
            notifyAllListenersNoNewOrderBook(currencyPair)
        }
    }

    private fun getOrderBookForCurrencyPair(currencyPair: CurrencyPair): OrderBook? {
        return try {
            userExchagneOrderBookService.getOrderBook(currencyPair)
        } catch (e: Exception) {
            logger.error("Error getting ticker at $exchangeName for currency pair $currencyPair: ${e.message}", e)
            null
        }
    }

    private fun notifyAllCurrencyPairListeners(currencyPair: CurrencyPair, orderBook: OrderBook) {
        listeners[currencyPair]!!.forEach {
            try {
                it.onOrderBook(orderBook)
            } catch (e: Exception) {
                logger.error("Error during notifying $exchangeName-$currencyPair ticker listener: ${e.message}", e)
            }
        }
    }

    private fun notifyAllListenersNoNewOrderBook(currencyPair: CurrencyPair, orderBook: OrderBook? = null) {
        listeners[currencyPair]!!.forEach {
            try {
                it.onNoNewOrderBook(orderBook)
            } catch (e: Exception) {
                logger.error("Error during notifying $exchangeName-$currencyPair ticker listener: ${e.message}", e)
            }
        }
    }

    private fun isNew(possiblyNewOrderBook: OrderBook, currencyPair: CurrencyPair): Boolean {
        val isNew = when (val lastOrderBook = lastOrderBook[currencyPair]) {
            null -> true
            else -> possiblyNewOrderBook != lastOrderBook
        }
        if (isNew) this.lastOrderBook[currencyPair] = possiblyNewOrderBook
        return isNew
    }

    override fun removeOrderBookListener(orderBookListener: OrderBookListener): Boolean {
        val currencyPair = orderBookListener.currencyPair()
        return if (listeners.containsKey(currencyPair)
                && listeners[currencyPair]!!.remove(orderBookListener)) {
            logger.info("Removed ticker listener for $exchangeName-$currencyPair")
            if (listeners[currencyPair]!!.isEmpty()) {
                listeners.remove(currencyPair)
                logger.info("No ticker listeners left for currency pair $exchangeName-$currencyPair, removed empty group")
            }
            true
        } else {
            logger.info("Ticker listener for $exchangeName-$currencyPair not removed, there was none matching")
            false
        }
    }

    override fun <T : OrderBookListener> getListenersOfClass(`class`: Class<T>): Map<CurrencyPair, Set<T>> {
        val result: MutableMap<CurrencyPair, MutableSet<T>> = mutableMapOf()
        listeners.keys.forEach { currencyPair ->
            listeners[currencyPair]?.forEach { tickerListener ->
                if (tickerListener.javaClass.isAssignableFrom(`class`)) {
                    if (!result.containsKey(currencyPair)) result[currencyPair] = mutableSetOf()
                    @Suppress("UNCHECKED_CAST")
                    result[currencyPair]?.add(tickerListener as T)
                }
            }
        }
        return result
    }

}
