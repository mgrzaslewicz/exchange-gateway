package automate.profit.autocoin.exchange.peruser

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.SupportedExchange.*
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.currency.toXchangeCurrencyPair
import automate.profit.autocoin.exchange.order.*
import mu.KLogging
import org.knowm.xchange.binance.service.BinanceCancelOrderParams
import org.knowm.xchange.dto.Order
import org.knowm.xchange.dto.trade.LimitOrder
import org.knowm.xchange.service.trade.params.CancelOrderParams
import org.knowm.xchange.service.trade.params.DefaultCancelOrderParamId
import org.knowm.xchange.service.trade.params.orders.DefaultOpenOrdersParamCurrencyPair
import java.math.BigDecimal
import java.time.Instant
import org.knowm.xchange.currency.CurrencyPair as XchangeCurrencyPair
import org.knowm.xchange.service.trade.TradeService as XchangeTradeService


interface UserExchangeTradeService {
    fun cancelOrder(params: ExchangeCancelOrderParams): Boolean
    fun placeBuyOrder(currencyPair: CurrencyPair, limitPrice: BigDecimal, amount: BigDecimal): ExchangeOrder
    fun placeSellOrder(currencyPair: CurrencyPair, limitPrice: BigDecimal, amount: BigDecimal): ExchangeOrder
    fun isOrderNotOpen(order: ExchangeOrder): Boolean
    fun getOpenOrders(): List<ExchangeOrder>
    fun getOpenOrders(currencyPair: CurrencyPair): List<ExchangeOrder> // TODO add fetching currency pairs that exist in wallet to get rid of passing currencyPair
}

fun XchangeCurrencyPair.toCurrencyPair() = CurrencyPair(base = this.base.currencyCode, counter = this.counter.currencyCode)

fun LimitOrder.toExchangeOrder(exchangeName: String) = ExchangeOrder(
        exchangeName = exchangeName,
        orderId = this.id,
        type = this.type.toExchangeOrderType(),
        orderedAmount = this.originalAmount,
        filledAmount = cumulativeAmount,
        price = this.averagePrice ?: this.limitPrice,
        currencyPair = this.currencyPair.toCurrencyPair(),
        status = this.status.toExchangeOrderStatus(),
        timestamp = this.timestamp?.toInstant()
)

fun ExchangeCancelOrderParams.xchangeOrderType(): Order.OrderType = when (this.orderType) {
    ExchangeOrderType.ASK_SELL -> Order.OrderType.ASK
    ExchangeOrderType.BID_BUY -> Order.OrderType.BID
}

open class XchangeUserExchangeTradeService(private val exchangeName: String,
                                           private val wrapped: XchangeTradeService) : UserExchangeTradeService {

    override fun getOpenOrders() = wrapped.getOpenOrders().allOpenOrders.filter { it is LimitOrder }.map { it as LimitOrder }.map { it.toExchangeOrder(exchangeName) }

    override fun getOpenOrders(currencyPair: CurrencyPair): List<ExchangeOrder> {
        val params = DefaultOpenOrdersParamCurrencyPair(currencyPair.toXchangeCurrencyPair())
        return wrapped.getOpenOrders(params).allOpenOrders.filter { it is LimitOrder }.map { (it as LimitOrder).toExchangeOrder(exchangeName) }
    }

    companion object : KLogging()

    override fun cancelOrder(params: ExchangeCancelOrderParams): Boolean {
        logger.info { "Requesting cancel order $params" }
        val cancelOrderParams: CancelOrderParams = getCancelOrderParams(params)
        return try {
            wrapped.cancelOrder(cancelOrderParams) && !isOrderStillOpen(params.currencyPair, params.orderId)
        } catch (e: Exception) {
            logger.error("Could not cancel order for $params. Exception: ${e.message}")
            false
        }
    }

    private fun getCancelOrderParams(params: ExchangeCancelOrderParams): CancelOrderParams {
        return when (SupportedExchange.fromExchangeName(exchangeName)) {
            BINANCE -> BinanceCancelOrderParams(params.currencyPair.toXchangeCurrencyPair(), params.orderId)

            KUCOIN,
            BITBAY,
            BITMEX,
            BITSTAMP,
            BITTREX,
            GATEIO,
            KRAKEN,
            POLONIEX,
            YOBIT -> DefaultCancelOrderParamId(params.orderId)

            else -> throw IllegalArgumentException("Exchange $exchangeName not handled for canceling orders")
        }
    }

    override fun isOrderNotOpen(order: ExchangeOrder): Boolean {
        if (logger.isInfoEnabled) logger.info("Requesting open orders")
        return try {
            !isOrderStillOpen(order)
        } catch (e: Exception) {
            if (logger.isErrorEnabled) logger.error("Getting open orders failed: ${e.message}", e)
            false
        }
    }

    private fun isOrderStillOpen(order: ExchangeOrder): Boolean {
        val openOrders = wrapped.getOpenOrders(DefaultOpenOrdersParamCurrencyPair(order.currencyPair.toXchangeCurrencyPair()))
        logger.info { "$openOrders" }
        val orderIsOnOpenOrderList = openOrders.openOrders.any { order.orderId == it.id }
        logger.info { "${order.type} order ${order.orderId} on open order list: $orderIsOnOpenOrderList" }
        return orderIsOnOpenOrderList
    }

    private fun isOrderStillOpen(currencyPair: CurrencyPair, orderId: String): Boolean {
        val openOrders = wrapped.getOpenOrders(DefaultOpenOrdersParamCurrencyPair(currencyPair.toXchangeCurrencyPair()))
        logger.info { "$openOrders" }
        val openOrderWithGivenId = openOrders.openOrders.find { orderId == it.id }
        val isOrderStillOpen = openOrderWithGivenId != null
        logger.info { "Order $orderId is still open: $isOrderStillOpen" }
        return isOrderStillOpen
    }

    /**
     * Buy currencyPair.base for currencyPair.base
     */
    override fun placeBuyOrder(currencyPair: CurrencyPair, limitPrice: BigDecimal, amount: BigDecimal): ExchangeOrder {
        val limitBuyOrder = LimitOrder.Builder(Order.OrderType.BID, currencyPair.toXchangeCurrencyPair()).orderStatus(Order.OrderStatus.NEW).limitPrice(limitPrice).originalAmount(amount).build()
        logger.info { "Requesting limit buy order: $limitBuyOrder" }

        val orderId = wrapped.placeLimitOrder(limitBuyOrder)

        logger.info { "Limit $exchangeName-buy order created with id: $orderId" }
        return ExchangeOrder(
                exchangeName = exchangeName,
                orderId = orderId,
                type = ExchangeOrderType.BID_BUY,
                orderedAmount = amount,
                filledAmount = BigDecimal.ZERO,
                price = limitPrice,
                currencyPair = currencyPair,
                status = ExchangeOrderStatus.NEW,
                timestamp = Instant.now()
        )
    }

    /**
     * Sell currencyPair.base currency and gain currencyPair.counter
     */
    override fun placeSellOrder(currencyPair: CurrencyPair, limitPrice: BigDecimal, amount: BigDecimal): ExchangeOrder {
        val limitSellOrder = LimitOrder.Builder(Order.OrderType.ASK, currencyPair.toXchangeCurrencyPair()).orderStatus(Order.OrderStatus.NEW).limitPrice(limitPrice).originalAmount(amount).build()
        logger.info { "Requesting limit sell order: $limitSellOrder" }

        val orderId = wrapped.placeLimitOrder(limitSellOrder)

        logger.info { "Limit $exchangeName-sell order created with id: $orderId" }
        return ExchangeOrder(
                exchangeName = exchangeName,
                orderId = orderId,
                type = ExchangeOrderType.ASK_SELL,
                orderedAmount = amount,
                filledAmount = BigDecimal.ZERO,
                price = limitPrice,
                currencyPair = currencyPair,
                status = ExchangeOrderStatus.NEW,
                timestamp = Instant.now()
        )
    }

}
