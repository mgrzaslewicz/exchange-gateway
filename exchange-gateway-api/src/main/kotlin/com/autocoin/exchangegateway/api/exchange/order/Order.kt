package com.autocoin.exchangegateway.api.exchange.order

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide
import com.autocoin.exchangegateway.spi.exchange.order.OrderStatus
import java.math.BigDecimal
import com.autocoin.exchangegateway.spi.exchange.order.Order as SpiOrder

data class Order(
    override val exchangeName: ExchangeName,
    override val exchangeOrderId: String,
    override val side: OrderSide,
    override val orderedAmount: BigDecimal,
    override val filledAmount: BigDecimal?,
    override val price: BigDecimal,
    override val currencyPair: CurrencyPair,
    override val status: OrderStatus,
    override val receivedAtMillis: Long,
    override val exchangeTimestampMillis: Long?,
) : SpiOrder

