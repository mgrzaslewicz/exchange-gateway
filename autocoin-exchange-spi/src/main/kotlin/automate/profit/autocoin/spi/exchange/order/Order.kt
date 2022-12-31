package automate.profit.autocoin.spi.exchange.order

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import java.math.BigDecimal

enum class OrderSide {
    ASK_SELL,
    BID_BUY
}

enum class OrderStatus {
    NEW,
    FILLED,
    PARTIALLY_FILLED,
    PARTIALLY_CANCELED,
    CANCELED,
    NOT_AVAILABLE
}

interface Order {
    val exchangeName: ExchangeName
    val exchangeOrderId: String
    val side: OrderSide
    val orderedAmount: BigDecimal
    val filledAmount: BigDecimal?
    val price: BigDecimal
    val currencyPair: CurrencyPair
    val status: OrderStatus
    val receivedAtMillis: Long
    val exchangeTimestampMillis: Long?
}
