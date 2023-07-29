package com.autocoin.exchangegateway.api.exchange.orderbook

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeyProvider
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.currency.ExchangeWithCurrencyPairStringCache
import com.autocoin.exchangegateway.spi.exchange.orderbook.gateway.OrderBookServiceGateway
import com.autocoin.exchangegateway.spi.exchange.orderbook.listener.OrderBookListeners
import com.autocoin.exchangegateway.spi.exchange.orderbook.listener.OrderBookRegistrationListener
import mu.KLogger
import mu.KotlinLogging
import java.lang.ref.SoftReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderBook as SpiOrderBook

interface PollingOrderBookFetcher<T> : OrderBookRegistrationListener {
    fun fetchOrderBooksThenNotifyListeners(exchange: Exchange)
}

class DefaultPollingOrderBookFetcher(
    private val orderBookServiceGateway: OrderBookServiceGateway<Exchange>,
    private val orderBookListeners: OrderBookListeners,
    private val apiKeyProvider: ApiKeyProvider<Exchange>,
    /** Avoid using shared threads between never-ending jobs.
     * When used fixed thread pool with the size of SupportedExchange.values().size,
     * it caused unnecessary delays and fetching was under rate limit
     */
    private val executorService: Map<Exchange, ExecutorService>,
    private val logger: KLogger = KotlinLogging.logger {},
    private val getOrderBookFrequentErrorLogFunction: (messageFunction: () -> String) -> Unit = { messageFunction ->
        logger.error(
            messageFunction,
        )
    },
) : PollingOrderBookFetcher<Exchange> {

    private val lastOrderBooks = ConcurrentHashMap<String, SoftReference<SpiOrderBook>>()
    private val runningFetchers = ConcurrentHashMap<Exchange, Future<*>>()

    override fun onListenerDeregistered(
        exchange: Exchange,
        currencyPair: CurrencyPair,
    ) {
    }

    override fun onLastListenerDeregistered(exchange: Exchange) {
        if (runningFetchers.containsKey(exchange)) {
            val scheduledFetcher = runningFetchers.getValue(exchange)
            scheduledFetcher.cancel(false)
            runningFetchers.remove(exchange)
        }
    }

    /**
     * Current synchronous fetcher implementation has to do nothing on currency pair registration as it fetches all currency pairs from exchange at one go
     */
    override fun onListenerRegistered(
        exchange: Exchange,
        currencyPair: CurrencyPair,
    ) {
    }

    override fun onFirstListenerRegistered(exchange: Exchange) {
        if (!runningFetchers.containsKey(exchange)) {
            val fetcher = executorService.getValue(exchange).submit {
                while (!Thread.currentThread().isInterrupted) {
                    fetchOrderBooksThenNotifyListeners(exchange)
                }
            }
            runningFetchers[exchange] = fetcher
        }
    }

    override fun fetchOrderBooksThenNotifyListeners(exchange: Exchange) {
        orderBookListeners.getOrderBookListeners(exchange).forEach { (currencyPair, orderBookListeners) ->
            val orderBook = getOrderBook(exchange, currencyPair)
            if (orderBook != null && isNew(orderBook, exchange, currencyPair)) {
                orderBookListeners.forEach {
                    it.onOrderBook(exchange, currencyPair, orderBook)
                }
            }
            else {
                orderBookListeners.forEach {
                    it.onNoNewOrderBook(exchange, currencyPair, orderBook)
                }
            }
        }
    }

    private fun getOrderBook(
        exchange: Exchange,
        currencyPair: CurrencyPair,
    ): SpiOrderBook? {
        return try {
            orderBookServiceGateway.getOrderBook(
                exchange = exchange,
                currencyPair = currencyPair,
                apiKey = apiKeyProvider.getApiKey(exchange),
            )
        } catch (e: Exception) {
            getOrderBookFrequentErrorLogFunction { "[$exchange-$currencyPair] Error getting order book: ${e.message} (${e.stackTrace[0]})" }
            null
        }
    }

    /**
     * Using SoftReference might result in a false positive - because reference might become null when too less available memory.
     * That will lead to recalculating order book based data, so the only down side might be slightly bigger CPU usage.
     */
    private fun isNew(
        possiblyNewOrderBook: SpiOrderBook,
        exchange: Exchange,
        currencyPair: CurrencyPair,
    ): Boolean {
        val key = ExchangeWithCurrencyPairStringCache.get(exchange.exchangeName + currencyPair)
        val isNew = when (val lastOrderBook = lastOrderBooks[key]?.get()) {
            null -> true
            else -> !possiblyNewOrderBook.deepEquals(lastOrderBook)
        }
        if (isNew) lastOrderBooks[key] = SoftReference(possiblyNewOrderBook)
        return isNew
    }
}
