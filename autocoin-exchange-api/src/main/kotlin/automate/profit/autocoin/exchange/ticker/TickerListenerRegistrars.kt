package automate.profit.autocoin.exchange.ticker

import automate.profit.autocoin.exchange.SupportedExchange
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


interface TickerListenerRegistrars {
    fun registerTickerListener(exchangeName: SupportedExchange, tickerListener: TickerListener): Boolean
    fun removeTickerListener(exchangeName: SupportedExchange, tickerListener: TickerListener): Boolean
    fun <T : TickerListener> getListenersOfClassList(clazz: Class<T>): List<T>
    fun fetchTickersAndNotifyListeners()
}

interface TickerListenerRegistrarProvider {
    fun createTickerListenerRegistrar(exchangeName: SupportedExchange): TickerListenerRegistrar
}

class DefaultTickerListenerRegistrars(
        initialTickerListenerRegistrarList: List<TickerListenerRegistrar>,
        private val tickerListenerRegistrarProvider: TickerListenerRegistrarProvider
) : TickerListenerRegistrars {

    private val tickerListenerRegistrarMap: MutableMap<SupportedExchange, TickerListenerRegistrar>

    override fun fetchTickersAndNotifyListeners() {
        runBlocking {
            val jobs = tickerListenerRegistrarMap.values.map { launch { it.fetchTickersAndNotifyListeners() } }
            jobs.forEach { it.join() }
        }
    }


    init {
        checkNoDuplicates(initialTickerListenerRegistrarList)
        tickerListenerRegistrarMap = initialTickerListenerRegistrarList.map { it.exchangeName to it }.toMap().toMutableMap()
    }

    private fun checkNoDuplicates(tickerListenerRegistrarList: List<TickerListenerRegistrar>) {
        val uniqueCount = tickerListenerRegistrarList.groupingBy { it.exchangeName }.eachCount()
        val duplicates = uniqueCount.filter { it.value > 1 }
        if (duplicates.isNotEmpty()) throw IllegalArgumentException("Configuration is invalid. There are duplicates: $duplicates")
    }

    override fun registerTickerListener(exchangeName: SupportedExchange, tickerListener: TickerListener): Boolean {
        return getTickerListenerRegistrar(exchangeName).registerTickerListener(tickerListener)
    }

    private fun getTickerListenerRegistrar(supportedExchange: SupportedExchange): TickerListenerRegistrar {
        return tickerListenerRegistrarMap.computeIfAbsent(supportedExchange) {
            tickerListenerRegistrarProvider.createTickerListenerRegistrar(supportedExchange)
        }
    }

    override fun removeTickerListener(exchangeName: SupportedExchange, tickerListener: TickerListener): Boolean {
        return getTickerListenerRegistrar(exchangeName).removeTickerListener(tickerListener)
    }

    override fun <T : TickerListener> getListenersOfClassList(clazz: Class<T>): List<T> {
        return tickerListenerRegistrarMap.values.flatMap { it.getListenersOfClass(clazz).values.flatten() }
    }

}
