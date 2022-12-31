package automate.profit.autocoin.exchange.order

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.order.Order
import org.knowm.xchange.dto.trade.LimitOrder

interface XchangeLimitOrderToOrderTransformer {
    operator fun invoke(exchangeName: ExchangeName, xchangeLimitOrder: LimitOrder, receivedAtMillis: Long): Order
}
