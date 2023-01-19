package com.autocoin.exchangegateway.api.exchange.ticker

import com.autocoin.exchangegateway.api.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.currency.ExchangeWithCurrencyPairStringCache
import com.autocoin.exchangegateway.spi.exchange.ticker.listener.TickerListeners
import com.autocoin.exchangegateway.spi.exchange.ticker.listener.TickerRegistrationListener
import mu.KLogging
import java.lang.ref.SoftReference
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import com.autocoin.exchangegateway.spi.exchange.ticker.Ticker as SpiTicker
import com.autocoin.exchangegateway.spi.exchange.ticker.gateway.TickerServiceGateway as SpiTickerService

interface SynchronousTickerFetchScheduler<T> : TickerRegistrationListener {
    fun fetchTickersThenNotifyListeners(exchangeName: ExchangeName)
}

class DefaultSynchronousTickerFetchScheduler<T>(
    private val allowedExchangeFetchFrequency: Map<ExchangeName, Duration>,
    private val tickerService: SpiTickerService<T>,
    private val apiKeys: Map<ExchangeName, ApiKeySupplier<T>>,
    private val tickerListeners: TickerListeners,
    /** Not 100% sure if separate threads are needed here in the same way as for fetching order books.
     * However, it proved to work well there so using it here too.*/
    private val executorService: Map<ExchangeName, ScheduledExecutorService>,
) : SynchronousTickerFetchScheduler<T> {
    companion object : KLogging()

    private val lastTickers = mutableMapOf<String, SoftReference<SpiTicker>>()
    private val scheduledFetchers = ConcurrentHashMap<ExchangeName, ScheduledFuture<*>>()

    override fun onLastListenerDeregistered(exchangeName: ExchangeName) {
        if (scheduledFetchers.containsKey(exchangeName)) {
            val scheduledFetcher = scheduledFetchers.getValue(exchangeName)
            scheduledFetcher.cancel(false)
            scheduledFetchers.remove(exchangeName)
        }
    }

    override fun onListenerDeregistered(
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
    ) {
    }

    override fun onFirstListenerRegistered(exchangeName: ExchangeName) {
        if (!scheduledFetchers.containsKey(exchangeName)) {
            val exchangeFrequency = allowedExchangeFetchFrequency.getValue(exchangeName)
            val scheduledFetcher = executorService.getValue(exchangeName).scheduleAtFixedRate(
                {
                    fetchTickersThenNotifyListeners(exchangeName)
                },
                0, exchangeFrequency.toMillis(), TimeUnit.MILLISECONDS,
            )
            scheduledFetchers[exchangeName] = scheduledFetcher
        }
    }

    override fun onListenerRegistered(
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
    ) {
    }

    override fun fetchTickersThenNotifyListeners(exchangeName: ExchangeName) {
        // TODO that's possibly subject to optimize and fetch all currency pairs with one exchange request where possible
        executorService.getValue(exchangeName).submit {
            tickerListeners.getTickerListeners(exchangeName).forEach { (currencyPair, tickerListeners) ->
                val ticker = getTicker(exchangeName, currencyPair)
                if (ticker != null && isNew(ticker, exchangeName, currencyPair)) {
                    tickerListeners.forEach {
                        it.onTicker(exchangeName, currencyPair, ticker)
                    }
                }
                else {
                    tickerListeners.forEach {
                        it.onNoNewTicker(exchangeName, currencyPair, ticker)
                    }
                }
            }
        }
    }

    private fun getTicker(
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
    ): SpiTicker? {
        return try {
            tickerService.getTicker(
                exchangeName = exchangeName,
                apiKey = apiKeys.getValue(exchangeName),
                currencyPair = currencyPair,
            )
        } catch (e: Exception) {
            logger.error { "[$exchangeName-$currencyPair] Error getting ticker: ${e.message} (${e.stackTrace[0]})" }
            null
        }
    }

    private fun isNew(
        possiblyNewTicker: SpiTicker,
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
    ): Boolean {
        val key = ExchangeWithCurrencyPairStringCache.get(exchangeName.value + currencyPair)
        val isNew = when (val lastTicker = lastTickers[key]?.get()) {
            null -> true
            else -> possiblyNewTicker != lastTicker
        }
        if (isNew) lastTickers[key] = SoftReference(possiblyNewTicker)
        return isNew
    }
}
