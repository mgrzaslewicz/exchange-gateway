package com.autocoin.exchangegateway.dto.order

import com.autocoin.exchangegateway.api.exchange.order.CancelOrdersParams

data class CancelOrdersParamsDto(
    val cancelOrdersParams: List<CancelOrderParamsDto>,
) {
    fun toCancelOrderParams() = CancelOrdersParams(
        cancelOrdersParams = cancelOrdersParams.map { it.toCancelOrderParams() },
    )
}
