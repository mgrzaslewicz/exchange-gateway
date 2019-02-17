package automate.profit.autocoin.exchange.peruser

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.ticker.TickerListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


interface TickerListenerRegistrars {
    fun registerTickerListener(supportedExchange: SupportedExchange, tickerListener: TickerListener): Boolean
    fun removeTickerListener(supportedExchange: SupportedExchange, tickerListener: TickerListener): Boolean
    fun <T : TickerListener> getListenersOfClassList(`class`: Class<T>): List<T>
    fun fetchTickersAndNotifyListeners()
}

interface TickerListenerRegistrarProvider {
    fun createTickerListenerRegistrar(supportedExchange: SupportedExchange): TickerListenerRegistrar
}

class DefaultTickerListenerRegistrarProvider(private val userExchangeServicesFactory: UserExchangeServicesFactory) : TickerListenerRegistrarProvider {
    override fun createTickerListenerRegistrar(supportedExchange: SupportedExchange): TickerListenerRegistrar {
        return userExchangeServicesFactory.createTickerListenerRegistrar(supportedExchange.exchangeName)
    }
}

class DefaultTickerListenerRegistrars(initialTickerListenerRegistrarList: List<TickerListenerRegistrar>, private val tickerListenerRegistrarProvider: TickerListenerRegistrarProvider) : TickerListenerRegistrars {

    private val tickerListenerRegistrarMap: MutableMap<SupportedExchange, TickerListenerRegistrar>

    override fun fetchTickersAndNotifyListeners() {
        runBlocking {
            val jobs = tickerListenerRegistrarMap.values.map { launch { it.fetchTickersAndNotifyListeners() } }
            jobs.forEach { it.join() }
        }
    }


    init {
        checkNoDuplicates(initialTickerListenerRegistrarList)
        tickerListenerRegistrarMap = initialTickerListenerRegistrarList.map { it.supportedExchange to it }.toMap().toMutableMap()
    }

    private fun checkNoDuplicates(tickerListenerRegistrarList: List<TickerListenerRegistrar>) {
        val uniqueCount = tickerListenerRegistrarList.groupingBy { it.supportedExchange }.eachCount()
        val duplicates = uniqueCount.filter { it.value > 1 }
        if (duplicates.isNotEmpty()) throw IllegalArgumentException("Configuration is invalid. There are duplicates: $duplicates")
    }

    override fun registerTickerListener(supportedExchange: SupportedExchange, tickerListener: TickerListener): Boolean {
        return getTickerListenerRegistrar(supportedExchange).registerTickerListener(tickerListener)
    }

    private fun getTickerListenerRegistrar(supportedExchange: SupportedExchange): TickerListenerRegistrar {
        return tickerListenerRegistrarMap.computeIfAbsent(supportedExchange) {
            tickerListenerRegistrarProvider.createTickerListenerRegistrar(supportedExchange)
        }
    }

    override fun removeTickerListener(supportedExchange: SupportedExchange, tickerListener: TickerListener): Boolean {
        return getTickerListenerRegistrar(supportedExchange).removeTickerListener(tickerListener)
    }

    override fun <T : TickerListener> getListenersOfClassList(clazz: Class<T>): List<T> {
        return tickerListenerRegistrarMap.values.flatMap { it.getListenersOfClass(clazz).values.flatten() }
    }

}
