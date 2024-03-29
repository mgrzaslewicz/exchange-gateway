package com.autocoin.exchangegateway.api.exchange.order

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide
import com.autocoin.exchangegateway.spi.exchange.order.CancelOrderParams as SpiCancelOrderParams

data class CancelOrderParams(
    override val exchange: Exchange,
    override val orderId: String,
    override val orderSide: OrderSide,
    override val currencyPair: CurrencyPair,
) : SpiCancelOrderParams
