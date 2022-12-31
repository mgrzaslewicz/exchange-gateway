package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.order.OrderSide
import automate.profit.autocoin.spi.exchange.orderbook.OrderInOrderBook
import org.knowm.xchange.dto.trade.LimitOrder
import java.util.function.Function
import org.knowm.xchange.currency.CurrencyPair as XchangeCurrencyPair
import org.knowm.xchange.dto.Order as XchangeOrder

interface XchangeLimitOrderToOrderInOrderBookTransformer {
    operator fun invoke(
        xchangeLimitOrder: LimitOrder,
        exchangeName: ExchangeName,
        receivedAtMillis: Long,
        xchangeOrderTypeTransformer: Function<XchangeOrder.OrderType, OrderSide>,
        xchangeCurrencyPairTransformer: Function<XchangeCurrencyPair, CurrencyPair>,
    ): OrderInOrderBook
}
