package com.autocoin.exchangegateway.api.exchange.order

import com.autocoin.exchangegateway.spi.exchange.Exchange
import org.knowm.xchange.dto.trade.LimitOrder

interface XchangeLimitOrderToOrderTransformer {
    operator fun invoke(
        exchange: Exchange,
        xchangeLimitOrder: LimitOrder,
        receivedAtMillis: Long,
    ): Order
}
