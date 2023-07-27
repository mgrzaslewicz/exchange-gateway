package com.autocoin.exchangegateway.dto.order

import com.autocoin.exchangegateway.api.exchange.order.CancelOrdersParams
import com.autocoin.exchangegateway.spi.exchange.ExchangeProvider

data class CancelOrdersParamsDto(
    val cancelOrdersParams: List<CancelOrderParamsDto>,
) {
    fun toCancelOrderParams(exchangeProvider: ExchangeProvider) = CancelOrdersParams(
        cancelOrdersParams = cancelOrdersParams.map { it.toCancelOrderParams(exchangeProvider) },
    )
}
