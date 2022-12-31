package automate.profit.autocoin.exchange.ticker

import automate.profit.autocoin.exchange.SupportedExchange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking


interface TickerListenerRegistrars {
    fun registerTickerListener(tickerListener: TickerListener): Boolean
    fun removeTickerListener(tickerListener: TickerListener): Boolean
    fun <T : TickerListener> getListenersOfClassList(clazz: Class<T>): List<T>
    fun fetchTickersAndNotifyListeners()
}


class DefaultTickerListenerRegistrars(
        initialTickerListenerRegistrarList: List<TickerListenerRegistrar>,
        private val tickerListenerRegistrarProvider: TickerListenerRegistrarProvider
) : TickerListenerRegistrars {

    private val tickerListenerRegistrarMap: MutableMap<SupportedExchange, TickerListenerRegistrar>

    override fun fetchTickersAndNotifyListeners() {
        runBlocking {
            val jobs = tickerListenerRegistrarMap.values.map {
                async(Dispatchers.IO) {
                    it.fetchTickersAndNotifyListeners()
                }
            }
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
        require(duplicates.isEmpty()) { "Configuration is invalid. There are duplicates: $duplicates" }
    }

    override fun registerTickerListener(tickerListener: TickerListener): Boolean {
        return getTickerListenerRegistrar(tickerListener.exchange()).registerTickerListener(tickerListener)
    }

    private fun getTickerListenerRegistrar(supportedExchange: SupportedExchange): TickerListenerRegistrar {
        return tickerListenerRegistrarMap.computeIfAbsent(supportedExchange) {
            tickerListenerRegistrarProvider.createTickerListenerRegistrar(supportedExchange)
        }
    }

    override fun removeTickerListener(tickerListener: TickerListener): Boolean {
        return getTickerListenerRegistrar(tickerListener.exchange()).removeTickerListener(tickerListener)
    }

    override fun <T : TickerListener> getListenersOfClassList(clazz: Class<T>): List<T> {
        return tickerListenerRegistrarMap.values.flatMap { it.getListenersOfClass(clazz).values.flatten() }
    }

}
