package automate.profit.autocoin.spi.exchange.orderbook.listener

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair


interface OrderBookListeners {
    fun addOrderBookListener(
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
        listener: OrderBookListener,
    ): Boolean

    fun removeOrderBookListener(
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
        listener: OrderBookListener,
    ): Boolean

    fun addOrderBookRegistrationListener(orderBookRegistrationListener: OrderBookRegistrationListener)
    fun removeOrderBookRegistrationListener(orderBookRegistrationListener: OrderBookRegistrationListener)
    fun getOrderBookListeners(exchangeName: ExchangeName): Map<CurrencyPair, Set<OrderBookListener>>
}
