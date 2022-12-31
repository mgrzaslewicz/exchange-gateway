package automate.profit.autocoin.spi.exchange.orderbook

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.order.OrderSide
import java.math.BigDecimal

interface OrderInOrderBook {
    val exchangeName: ExchangeName
    val side: OrderSide
    val orderedAmount: BigDecimal
    val price: BigDecimal
    val currencyPair: CurrencyPair
    val receivedAtMillis: Long
    val exchangeTimestampMillis: Long?
    fun valueInCounterCurrency(): BigDecimal = orderedAmount.multiply(price)
}
