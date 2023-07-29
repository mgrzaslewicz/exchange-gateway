package com.autocoin.exchangegateway.api.exchange.ticker

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeyProvider
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.currency.ExchangeWithCurrencyPairStringCache
import com.autocoin.exchangegateway.spi.exchange.ticker.gateway.TickerServiceGateway
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

interface PollingTickerFetcher<T> : TickerRegistrationListener {
    fun fetchTickersThenNotifyListeners(exchange: Exchange)
}

class DefaultPollingTickerFetcher(
    // TODO remove it and use rate limited implementation of ticker service gateway instead
    private val allowedExchangeFetchFrequency: Map<Exchange, Duration>,
    private val tickerServiceGateway: TickerServiceGateway<Exchange>,
    private val apiKeyProvider: ApiKeyProvider<Exchange>,
    private val tickerListeners: TickerListeners,
    /** Not 100% sure if separate threads are needed here in the same way as for fetching order books.
     * However, it proved to work well there so using it here too.*/
    private val executorService: Map<Exchange, ScheduledExecutorService>,
) : PollingTickerFetcher<Exchange> {
    companion object : KLogging()

    private val lastTickers = mutableMapOf<String, SoftReference<SpiTicker>>()
    private val scheduledFetchers = ConcurrentHashMap<Exchange, ScheduledFuture<*>>()

    override fun onLastListenerDeregistered(exchange: Exchange) {
        if (scheduledFetchers.containsKey(exchange)) {
            val scheduledFetcher = scheduledFetchers.getValue(exchange)
            scheduledFetcher.cancel(false)
            scheduledFetchers.remove(exchange)
        }
    }

    override fun onListenerDeregistered(
        exchange: Exchange,
        currencyPair: CurrencyPair,
    ) {
    }

    override fun onFirstListenerRegistered(exchange: Exchange) {
        if (!scheduledFetchers.containsKey(exchange)) {
            val exchangeFrequency = allowedExchangeFetchFrequency.getValue(exchange)
            val scheduledFetcher = executorService.getValue(exchange).scheduleAtFixedRate(
                {
                    fetchTickersThenNotifyListeners(exchange)
                },
                0, exchangeFrequency.toMillis(), TimeUnit.MILLISECONDS,
            )
            scheduledFetchers[exchange] = scheduledFetcher
        }
    }

    override fun onListenerRegistered(
        exchange: Exchange,
        currencyPair: CurrencyPair,
    ) {
    }

    override fun fetchTickersThenNotifyListeners(exchange: Exchange) {
        // TODO that's possibly subject to optimize and fetch all currency pairs with one exchange request where possible
        executorService.getValue(exchange).submit {
            tickerListeners.getTickerListeners(exchange).forEach { (currencyPair, tickerListeners) ->
                val ticker = getTicker(exchange, currencyPair)
                if (ticker != null && isNew(ticker, exchange, currencyPair)) {
                    tickerListeners.forEach {
                        it.onTicker(exchange, currencyPair, ticker)
                    }
                }
                else {
                    tickerListeners.forEach {
                        it.onNoNewTicker(exchange, currencyPair, ticker)
                    }
                }
            }
        }
    }

    private fun getTicker(
        exchange: Exchange,
        currencyPair: CurrencyPair,
    ): SpiTicker? {
        return try {
            tickerServiceGateway.getTicker(
                exchange = exchange,
                apiKey = apiKeyProvider.getApiKey(exchange),
                currencyPair = currencyPair,
            )
        } catch (e: Exception) {
            logger.error { "[$exchange-$currencyPair] Error getting ticker: ${e.message} (${e.stackTrace[0]})" }
            null
        }
    }

    private fun isNew(
        possiblyNewTicker: SpiTicker,
        exchange: Exchange,
        currencyPair: CurrencyPair,
    ): Boolean {
        val key = ExchangeWithCurrencyPairStringCache.get(exchange.exchangeName + currencyPair)
        val isNew = when (val lastTicker = lastTickers[key]?.get()) {
            null -> true
            else -> possiblyNewTicker != lastTicker
        }
        if (isNew) lastTickers[key] = SoftReference(possiblyNewTicker)
        return isNew
    }
}
