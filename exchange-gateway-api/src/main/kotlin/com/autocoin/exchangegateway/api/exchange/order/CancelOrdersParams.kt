package com.autocoin.exchangegateway.api.exchange.order

import com.autocoin.exchangegateway.spi.exchange.order.CancelOrderParams
import com.autocoin.exchangegateway.spi.exchange.order.CancelOrdersParams as SpiCancelOrdersParams

data class CancelOrdersParams(
    override val cancelOrdersParams: List<CancelOrderParams>,
) : SpiCancelOrdersParams
