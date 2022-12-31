package automate.profit.autocoin.exchange

import org.junit.jupiter.api.Test

class SupportedExchangeToXchangeJavaClassTest {
    @Test
    fun shouldAllSupportedExchangesHaveClassAssigned() {
        SupportedExchange.values().forEach {
            it.toXchangeJavaClass() // will throw exception when not mapped
        }
    }
}
