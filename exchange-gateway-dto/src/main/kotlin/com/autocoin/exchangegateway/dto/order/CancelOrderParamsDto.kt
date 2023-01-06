package com.autocoin.exchangegateway.dto.order

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.api.exchange.order.CancelOrderParams
import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide


data class CancelOrderParamsDto(
    val exchangeName: String,
    val orderId: String,
    val orderSide: String,
    val currencyPair: CurrencyPairDto,
) {
    fun toCancelOrderParams() = CancelOrderParams(
        orderId = orderId,
        orderSide = OrderSide.valueOf(orderSide),
        exchangeName = ExchangeName(exchangeName),
        currencyPair = CurrencyPair.of(currencyPair.base, currencyPair.counter),
    )
}
