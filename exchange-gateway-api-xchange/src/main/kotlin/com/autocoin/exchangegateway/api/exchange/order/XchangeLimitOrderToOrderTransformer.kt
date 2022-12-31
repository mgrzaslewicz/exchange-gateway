package com.autocoin.exchangegateway.api.exchange.order

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import org.knowm.xchange.dto.trade.LimitOrder

interface XchangeLimitOrderToOrderTransformer {
    operator fun invoke(
        exchangeName: ExchangeName,
        xchangeLimitOrder: LimitOrder,
        receivedAtMillis: Long,
    ): Order
}
