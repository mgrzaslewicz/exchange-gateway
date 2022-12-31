package automate.profit.autocoin.spi.exchange.currency

import java.util.concurrent.ConcurrentHashMap

open class Cache<K, V> {

    private val cache = ConcurrentHashMap<K, V>()

    fun get(key: K, valueFunction: () -> V): V {
        return cache.computeIfAbsent(key) {
            valueFunction()
        }
    }

}
