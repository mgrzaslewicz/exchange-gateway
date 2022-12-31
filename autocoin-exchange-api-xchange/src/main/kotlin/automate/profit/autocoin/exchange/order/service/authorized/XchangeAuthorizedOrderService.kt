package automate.profit.autocoin.exchange.order.service.authorized

import automate.profit.autocoin.api.exchange.order.Order
import automate.profit.autocoin.exchange.order.XchangeLimitOrderToOrderTransformer
import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.order.CancelOrderParams
import automate.profit.autocoin.spi.exchange.order.OrderSide
import automate.profit.autocoin.spi.exchange.order.OrderStatus
import automate.profit.autocoin.spi.exchange.order.service.authorized.AuthorizedOrderService
import org.knowm.xchange.dto.trade.LimitOrder
import org.knowm.xchange.dto.trade.MarketOrder
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParamCurrencyPair
import java.math.BigDecimal
import java.time.Clock
import java.util.function.Function
import automate.profit.autocoin.spi.exchange.order.Order as SpiOrder
import org.knowm.xchange.service.trade.TradeService as XchangeTradeService
import org.knowm.xchange.service.trade.params.CancelOrderParams as XchangeCancelOrderParams


class XchangeAuthorizedOrderService(
    override val exchangeName: ExchangeName,
    val delegate: XchangeTradeService,
    private val cancelOrderParamsToXchangeParams: Function<CancelOrderParams, XchangeCancelOrderParams>,
    private val openOrdersCurrencyPairParamsToXchangeParams: Function<CurrencyPair, OpenOrdersParamCurrencyPair>,
    private val currencyPairToXchange: Function<CurrencyPair, org.knowm.xchange.currency.CurrencyPair> ,
    private val xchangeLimitOrderToOrderTransformer: XchangeLimitOrderToOrderTransformer ,
    private val clock: Clock,
) : AuthorizedOrderService {
    override fun cancelOrder(cancelOrderParams: CancelOrderParams): Boolean {
        val xchangeCancelOrderParams = cancelOrderParamsToXchangeParams.apply(cancelOrderParams)
        return delegate.cancelOrder(xchangeCancelOrderParams)
    }

    override fun placeMarketBuyOrderWithCounterCurrencyAmount(currencyPair: CurrencyPair, counterCurrencyAmount: BigDecimal, currentPrice: BigDecimal): SpiOrder {
        TODO("Implement it for exchanges that supports this kind of order")
    }

    override fun placeMarketBuyOrderWithBaseCurrencyAmount(currencyPair: CurrencyPair, baseCurrencyAmount: BigDecimal, currentPrice: BigDecimal): Order {
        val marketBuyOrder = MarketOrder.Builder(org.knowm.xchange.dto.Order.OrderType.BID, currencyPairToXchange.apply(currencyPair))
            .orderStatus(org.knowm.xchange.dto.Order.OrderStatus.NEW)
            .originalAmount(baseCurrencyAmount)
            .build()

        val orderId = delegate.placeMarketOrder(marketBuyOrder)
        val receivedAtMillis = clock.millis()

        return Order(
            exchangeName = exchangeName,
            exchangeOrderId = orderId,
            side = OrderSide.BID_BUY,
            orderedAmount = baseCurrencyAmount,
            filledAmount = BigDecimal.ZERO,
            price = currentPrice,
            currencyPair = currencyPair,
            status = OrderStatus.NEW,
            receivedAtMillis = receivedAtMillis,
            exchangeTimestampMillis = null,
        )
    }

    override fun placeMarketSellOrderWithCounterCurrencyAmount(currencyPair: CurrencyPair, counterCurrencyAmount: BigDecimal, currentPrice: BigDecimal): SpiOrder {
        TODO("Implement it for exchanges that supports this kind of order")
    }

    override fun placeMarketSellOrderWithBaseCurrencyAmount(currencyPair: CurrencyPair, baseCurrencyAmount: BigDecimal, currentPrice: BigDecimal): Order {
        val marketSellOrder = MarketOrder.Builder(org.knowm.xchange.dto.Order.OrderType.ASK, currencyPairToXchange.apply(currencyPair))
            .orderStatus(org.knowm.xchange.dto.Order.OrderStatus.NEW)
            .originalAmount(baseCurrencyAmount)
            .build()

        val orderId = delegate.placeMarketOrder(marketSellOrder)
        val receivedAtMillis = clock.millis()

        return Order(
            exchangeName = exchangeName,
            exchangeOrderId = orderId,
            side = OrderSide.ASK_SELL,
            orderedAmount = baseCurrencyAmount,
            filledAmount = BigDecimal.ZERO,
            price = currentPrice,
            currencyPair = currencyPair,
            status = OrderStatus.NEW,
            receivedAtMillis = receivedAtMillis,
            exchangeTimestampMillis = null,
        )
    }

    override fun getOpenOrders(): List<SpiOrder> {
        return delegate.getOpenOrders()
            .allOpenOrders
            .filterIsInstance<LimitOrder>()
            .map {
                xchangeLimitOrderToOrderTransformer(
                    exchangeName = exchangeName,
                    xchangeLimitOrder = it,
                    receivedAtMillis = clock.millis(),
                )
            }
    }

    override fun getOpenOrders(currencyPair: CurrencyPair): List<SpiOrder> {
        val params = openOrdersCurrencyPairParamsToXchangeParams.apply(currencyPair)
        return delegate.getOpenOrders(params)
            .allOpenOrders
            .filterIsInstance<LimitOrder>()
            .map {
                xchangeLimitOrderToOrderTransformer(
                    exchangeName = exchangeName,
                    xchangeLimitOrder = it,
                    receivedAtMillis = clock.millis(),
                )
            }
    }

    override fun placeLimitBuyOrder(currencyPair: CurrencyPair, buyPrice: BigDecimal, amount: BigDecimal): Order {
        val limitBuyOrder = LimitOrder.Builder(org.knowm.xchange.dto.Order.OrderType.BID, currencyPairToXchange.apply(currencyPair))
            .orderStatus(org.knowm.xchange.dto.Order.OrderStatus.NEW)
            .limitPrice(buyPrice)
            .originalAmount(amount)
            .build()
        val orderId = delegate.placeLimitOrder(limitBuyOrder)
        val receivedAtMillis = clock.millis()

        return Order(
            exchangeName = exchangeName,
            exchangeOrderId = orderId,
            side = OrderSide.BID_BUY,
            orderedAmount = amount,
            filledAmount = BigDecimal.ZERO,
            price = buyPrice,
            currencyPair = currencyPair,
            status = OrderStatus.NEW,
            receivedAtMillis = receivedAtMillis,
            exchangeTimestampMillis = null,
        )
    }

    override fun placeLimitSellOrder(currencyPair: CurrencyPair, sellPrice: BigDecimal, amount: BigDecimal): Order {
        val limitSellOrder = LimitOrder.Builder(org.knowm.xchange.dto.Order.OrderType.ASK, currencyPairToXchange.apply(currencyPair))
            .orderStatus(org.knowm.xchange.dto.Order.OrderStatus.NEW)
            .limitPrice(sellPrice)
            .originalAmount(amount)
            .build()
        val orderId = delegate.placeLimitOrder(limitSellOrder)
        val receivedAtMillis = clock.millis()

        return Order(
            exchangeName = exchangeName,
            exchangeOrderId = orderId,
            side = OrderSide.ASK_SELL,
            orderedAmount = amount,
            filledAmount = BigDecimal.ZERO,
            price = sellPrice,
            currencyPair = currencyPair,
            status = OrderStatus.NEW,
            receivedAtMillis = receivedAtMillis,
            exchangeTimestampMillis = null,
        )
    }

}

