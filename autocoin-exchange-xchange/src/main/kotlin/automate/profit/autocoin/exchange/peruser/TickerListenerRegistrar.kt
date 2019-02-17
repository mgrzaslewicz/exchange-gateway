package automate.profit.autocoin.exchange.peruser

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.currency.toXchangeCurrencyPair
import automate.profit.autocoin.ticker.Ticker
import automate.profit.autocoin.ticker.TickerListener
import automate.profit.autocoin.ticker.toTicker
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.knowm.xchange.service.marketdata.MarketDataService

interface TickerListenerRegistrar {
    fun registerTickerListener(tickerListener: TickerListener): Boolean
    fun fetchTickersAndNotifyListeners()
    fun removeTickerListener(tickerListener: TickerListener): Boolean
    fun <T : TickerListener> getListenersOfClass(`class`: Class<T>): Map<CurrencyPair, Set<T>>
    val supportedExchange: SupportedExchange
}

interface UserExchangeTickerProvider {
    fun getTicker(currencyPair: CurrencyPair): Ticker
}

class XchangeUserExchangeTickerProvider(private val marketDataService: MarketDataService) : UserExchangeTickerProvider {
    override fun getTicker(currencyPair: CurrencyPair): Ticker {
        return marketDataService.getTicker(currencyPair.toXchangeCurrencyPair()).toTicker()
    }
}

class DefaultTickerListenerRegistrar(override val supportedExchange: SupportedExchange, private val userExchangeTickerProvider
: UserExchangeTickerProvider) : TickerListenerRegistrar {

    private val logger = KotlinLogging.logger("${DefaultTickerListenerRegistrar::class.java.name}.$supportedExchange")

    private val currencyPairListeners = mutableMapOf<CurrencyPair, MutableSet<TickerListener>>()

    private val lastTicker = mutableMapOf<CurrencyPair, Ticker>()

    override fun registerTickerListener(tickerListener: TickerListener): Boolean {
        val currencyPair = tickerListener.currencyPair()
        logger.info("Registering listener for $supportedExchange-$currencyPair")
        return if (!currencyPairListeners.contains(currencyPair)) {
            currencyPairListeners[currencyPair] = mutableSetOf(tickerListener)
            logger.info("Registered first listener for $supportedExchange-$currencyPair")
            true
        } else if (currencyPairListeners[currencyPair]!!.add(tickerListener)) {
            logger.info("Registered another listener for $supportedExchange-$currencyPair")
            true
        } else {
            logger.warn("Registered another listener for $supportedExchange-$currencyPair")
            false
        }
    }

    override fun fetchTickersAndNotifyListeners() {
        runBlocking {
            val jobs = mutableListOf<Job>()
            currencyPairListeners.keys.forEach { currencyPair ->
                jobs += launch { fetchTickerAndNotifyListeners(currencyPair) }
            }
            logger.info("Waiting for all currencies at $supportedExchange...")
            jobs.forEach { it.join() }
        }
        logger.info("All currencies done at $supportedExchange.")
    }

    private fun fetchTickerAndNotifyListeners(currencyPair: CurrencyPair) {
        val ticker = getTickerForCurrencyPair(currencyPair)
        if (ticker != null) {
            if (isNew(ticker)) {
                logger.info { "New ticker at $supportedExchange: $ticker" }
                notifyAllCurrencyPairListeners(currencyPair, ticker)
            } else {
                logger.info("No new ticker at $supportedExchange for currency pair ${ticker.currencyPair}. Last timestamp: ${lastTicker[ticker.currencyPair]?.timestamp}, ${lastTicker[ticker.currencyPair]?.timestamp?.epochSecond}")
            }
        }
    }

    private fun getTickerForCurrencyPair(currencyPair: CurrencyPair): Ticker? {
        return try {
            userExchangeTickerProvider.getTicker(currencyPair)
        } catch (e: Exception) {
            logger.error("Error getting ticker at $supportedExchange for currency pair $currencyPair: ${e.message}", e)
            null
        }
    }

    private fun notifyAllCurrencyPairListeners(currencyPair: CurrencyPair, ticker: Ticker) {
        currencyPairListeners[currencyPair]!!.forEach {
            try {
                it.onTicker(ticker)
            } catch (e: Exception) {
                logger.error("Error during notifying $supportedExchange-$currencyPair ticker listener: ${e.message}", e)
            }
        }
    }

    private fun isNew(possiblyNewTicker: Ticker): Boolean {
        val lastTicker = lastTicker[possiblyNewTicker.currencyPair]
        val isNew = when {
            lastTicker == null -> true
            possiblyNewTicker.hasTimestamp() && lastTicker.hasTimestamp() -> isNewByTimestamp(possiblyNewTicker, lastTicker)
            else -> possiblyNewTicker != lastTicker
        }
        if (isNew) this.lastTicker[possiblyNewTicker.currencyPair] = possiblyNewTicker
        return isNew
    }

    private fun isNewByTimestamp(possiblyNewTicker: Ticker, lastTicker: Ticker): Boolean {
        return possiblyNewTicker.timestamp!!.epochSecond > lastTicker.timestamp!!.epochSecond
    }

    override fun removeTickerListener(tickerListener: TickerListener): Boolean {
        val currencyPair = tickerListener.currencyPair()
        return if (currencyPairListeners.containsKey(currencyPair)
                && currencyPairListeners[currencyPair]!!.remove(tickerListener)) {
            logger.info("Removed ticker listener for $supportedExchange-$currencyPair")
            if (currencyPairListeners[currencyPair]!!.isEmpty()) {
                currencyPairListeners.remove(currencyPair)
                logger.info("No ticker listeners left for currency pair $supportedExchange-$currencyPair, removed empty group")
            }
            true
        } else {
            logger.info("Ticker listener for $supportedExchange-$currencyPair not removed, there was none matching")
            false
        }
    }

    override fun <T : TickerListener> getListenersOfClass(`class`: Class<T>): Map<CurrencyPair, Set<T>> {
        val result: MutableMap<CurrencyPair, MutableSet<T>> = mutableMapOf()
        currencyPairListeners.keys.forEach { currencyPair ->
            currencyPairListeners[currencyPair]?.forEach { tickerListener ->
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
