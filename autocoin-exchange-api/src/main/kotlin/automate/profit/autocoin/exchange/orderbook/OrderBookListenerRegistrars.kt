package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.SupportedExchange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking


interface OrderBookListenerRegistrars {
    fun registerOrderBookListener(listener: OrderBookListener): Boolean
    fun removeOrderBookListener(listener: OrderBookListener): Boolean
    fun <T : OrderBookListener> getListenersOfClassList(clazz: Class<T>): List<T>
    fun fetchOrderBooksAndNotifyListeners()
}


class DefaultOrderBookListenerRegistrars(
        initialTickerListenerRegistrarList: List<OrderBookListenerRegistrar>,
        private val orderBookListenerRegistrarProvider: OrderBookListenerRegistrarProvider
) : OrderBookListenerRegistrars {

    private val listenerRegistrarMap: MutableMap<SupportedExchange, OrderBookListenerRegistrar>

    init {
        checkNoDuplicates(initialTickerListenerRegistrarList)
        listenerRegistrarMap = initialTickerListenerRegistrarList.map { it.exchangeName to it }.toMap().toMutableMap()
    }

    private fun checkNoDuplicates(tickerListenerRegistrarList: List<OrderBookListenerRegistrar>) {
        val uniqueCount = tickerListenerRegistrarList.groupingBy { it.exchangeName }.eachCount()
        val duplicates = uniqueCount.filter { it.value > 1 }
        require(duplicates.isEmpty()) { "Configuration is invalid. There are duplicates: $duplicates" }
    }

    override fun fetchOrderBooksAndNotifyListeners() {
        runBlocking {
            val jobs = listenerRegistrarMap.values.map {
                async(Dispatchers.IO) {
                    it.fetchOrderBooksAndNotifyListeners()
                }
            }
            jobs.forEach { it.join() }
        }
    }


    override fun registerOrderBookListener(listener: OrderBookListener): Boolean {
        return getOrderBookListenerRegistrar(listener.exchange()).registerOrderBookListener(listener)
    }

    private fun getOrderBookListenerRegistrar(supportedExchange: SupportedExchange): OrderBookListenerRegistrar {
        return listenerRegistrarMap.computeIfAbsent(supportedExchange) {
            orderBookListenerRegistrarProvider.createOrderBookListenerRegistrar(supportedExchange)
        }
    }

    override fun removeOrderBookListener(tickerListener: OrderBookListener): Boolean {
        return getOrderBookListenerRegistrar(tickerListener.exchange()).removeOrderBookListener(tickerListener)
    }

    override fun <T : OrderBookListener> getListenersOfClassList(clazz: Class<T>): List<T> {
        return listenerRegistrarMap.values.flatMap { it.getListenersOfClass(clazz).values.flatten() }
    }

}
