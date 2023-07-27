package com.autocoin.exchangegateway.spi.exchange.order

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair

/**
 * Having just orderId is not enough.
 * Some exchanges require more parameters when canceling the order
 */
interface CancelOrderParams {
    val exchange: Exchange
    val orderId: String
    val orderSide: OrderSide
    val currencyPair: CurrencyPair
}
