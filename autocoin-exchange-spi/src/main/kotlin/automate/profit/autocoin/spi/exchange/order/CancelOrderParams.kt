package automate.profit.autocoin.spi.exchange.order

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair

/**
 * Having just orderId is not enough.
 * Some exchanges require more parameters when canceling the order
 */
interface CancelOrderParams {
    val exchangeName: ExchangeName
    val orderId: String
    val orderSide: OrderSide
    val currencyPair: CurrencyPair
}
