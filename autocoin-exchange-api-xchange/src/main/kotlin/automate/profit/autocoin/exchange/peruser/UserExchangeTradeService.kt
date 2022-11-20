package automate.profit.autocoin.exchange.peruser

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.SupportedExchange.*
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.currency.toXchangeCurrencyPair
import automate.profit.autocoin.exchange.order.*
import automate.profit.autocoin.exchange.orderbook.OrderBookExchangeOrder
import automate.profit.autocoin.exchange.ratelimiter.ExchangeRateLimiter
import automate.profit.autocoin.exchange.ratelimiter.RateLimiterBehavior
import automate.profit.autocoin.exchange.ratelimiter.RateLimiterBehavior.WAIT_WITHOUT_TIMEOUT
import automate.profit.autocoin.exchange.ratelimiter.acquireWith
import mu.KLogging
import org.knowm.xchange.binance.service.BinanceCancelOrderParams
import org.knowm.xchange.dto.Order
import org.knowm.xchange.dto.trade.LimitOrder
import org.knowm.xchange.dto.trade.MarketOrder
import org.knowm.xchange.service.trade.params.CancelOrderParams
import org.knowm.xchange.service.trade.params.DefaultCancelOrderParamId
import org.knowm.xchange.service.trade.params.orders.DefaultOpenOrdersParamCurrencyPair
import java.math.BigDecimal
import java.time.Clock
import org.knowm.xchange.currency.CurrencyPair as XchangeCurrencyPair
import org.knowm.xchange.service.trade.TradeService as XchangeTradeService


interface UserExchangeTradeService {
    fun cancelOrder(params: ExchangeCancelOrderParams, rateLimiterBehaviour: RateLimiterBehavior = WAIT_WITHOUT_TIMEOUT): Boolean
    fun placeLimitBuyOrder(
        currencyPair: CurrencyPair,
        limitPrice: BigDecimal,
        amount: BigDecimal,
        rateLimiterBehaviour: RateLimiterBehavior = WAIT_WITHOUT_TIMEOUT
    ): ExchangeOrder


    fun placeLimitSellOrder(
        currencyPair: CurrencyPair,
        limitPrice: BigDecimal,
        amount: BigDecimal,
        rateLimiterBehaviour: RateLimiterBehavior = WAIT_WITHOUT_TIMEOUT
    ): ExchangeOrder

    fun placeMarketBuyOrderWithBaseCurrencyAmount(
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        rateLimiterBehaviour: RateLimiterBehavior = WAIT_WITHOUT_TIMEOUT
    ): ExchangeOrder

    fun placeMarketBuyOrderWithCounterCurrencyAmount(
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        rateLimiterBehaviour: RateLimiterBehavior = WAIT_WITHOUT_TIMEOUT
    ): ExchangeOrder

    fun placeMarketSellOrderWithBaseCurrencyAmount(
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        rateLimiterBehaviour: RateLimiterBehavior = WAIT_WITHOUT_TIMEOUT
    ): ExchangeOrder

    fun placeMarketSellOrderWithCounterCurrencyAmount(
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        rateLimiterBehaviour: RateLimiterBehavior = WAIT_WITHOUT_TIMEOUT
    ): ExchangeOrder

    fun isOrderNotOpen(order: ExchangeOrder, rateLimiterBehaviour: RateLimiterBehavior = WAIT_WITHOUT_TIMEOUT): Boolean
    fun getOpenOrders(rateLimiterBehaviour: RateLimiterBehavior = WAIT_WITHOUT_TIMEOUT): List<ExchangeOrder>
    fun getOpenOrders(
        currencyPair: CurrencyPair,
        rateLimiterBehaviour: RateLimiterBehavior = WAIT_WITHOUT_TIMEOUT
    ): List<ExchangeOrder> // TODO add fetching currency pairs that exist in wallet to get rid of passing currencyPair
}

fun XchangeCurrencyPair.toCurrencyPair() = CurrencyPair.of(base = this.base.currencyCode, counter = this.counter.currencyCode)

fun LimitOrder.toExchangeOrder(exchangeName: String, receivedAtMillis: Long) = ExchangeOrder(
    exchangeName = exchangeName,
    orderId = this.id,
    type = this.type.toExchangeOrderType(),
    orderedAmount = this.originalAmount,
    filledAmount = cumulativeAmount,
    price = this.averagePrice ?: this.limitPrice,
    currencyPair = this.currencyPair.toCurrencyPair(),
    status = this.status.toExchangeOrderStatus(),
    receivedAtMillis = receivedAtMillis,
    exchangeTimestampMillis = this.timestamp?.time
)

fun LimitOrder.toOrderBookExchangeOrder(exchangeName: String, receivedAtMillis: Long) = OrderBookExchangeOrder(
    exchangeName = exchangeName,
    type = this.type.toExchangeOrderType(),
    orderedAmount = this.originalAmount,
    price = this.averagePrice ?: this.limitPrice,
    currencyPair = this.currencyPair.toCurrencyPair(),
    receivedAtMillis = receivedAtMillis,
    exchangeTimestampMillis = this.timestamp?.time
)

fun ExchangeCancelOrderParams.xchangeOrderType(): Order.OrderType = when (this.orderType) {
    ExchangeOrderType.ASK_SELL -> Order.OrderType.ASK
    ExchangeOrderType.BID_BUY -> Order.OrderType.BID
}

open class XchangeUserExchangeTradeService(
    private val exchangeName: String,
    val wrapped: XchangeTradeService,
    private val exchangeRateLimiter: ExchangeRateLimiter,
    private val clock: Clock,
) : UserExchangeTradeService {

    override fun getOpenOrders(rateLimiterBehaviour: RateLimiterBehavior): List<ExchangeOrder> {
        exchangeRateLimiter.acquireWith(rateLimiterBehaviour) { "[$exchangeName] Could not acquire permit to getOpenOrders" }
        return wrapped.getOpenOrders()
            .allOpenOrders
            .filterIsInstance<LimitOrder>()
            .map { it.toExchangeOrder(exchangeName, clock.millis()) }
    }

    override fun getOpenOrders(currencyPair: CurrencyPair, rateLimiterBehaviour: RateLimiterBehavior): List<ExchangeOrder> {
        exchangeRateLimiter.acquireWith(rateLimiterBehaviour) { "[$exchangeName-$currencyPair] Could not acquire permit to getOpenOrders" }
        val params = DefaultOpenOrdersParamCurrencyPair(currencyPair.toXchangeCurrencyPair())
        return wrapped.getOpenOrders(params)
            .allOpenOrders
            .filterIsInstance<LimitOrder>()
            .map { it.toExchangeOrder(exchangeName, clock.millis()) }
    }

    companion object : KLogging()

    override fun cancelOrder(params: ExchangeCancelOrderParams, rateLimiterBehaviour: RateLimiterBehavior): Boolean {
        logger.info { "Requesting cancel order $params" }
        val cancelOrderParams: CancelOrderParams = getCancelOrderParams(params)
        return try {
            exchangeRateLimiter.acquireWith(rateLimiterBehaviour) { "[$exchangeName] Could not acquire permit to cancelOrder" }
            wrapped.cancelOrder(cancelOrderParams) && !isOrderStillOpen(
                currencyPair = params.currencyPair,
                orderId = params.orderId,
                rateLimiterBehaviour = rateLimiterBehaviour
            )
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

    override fun isOrderNotOpen(order: ExchangeOrder, rateLimiterBehaviour: RateLimiterBehavior): Boolean {
        if (logger.isInfoEnabled) logger.info("Requesting open orders")
        return try {
            !isOrderStillOpen(order, rateLimiterBehaviour)
        } catch (e: Exception) {
            if (logger.isErrorEnabled) logger.error("Getting open orders failed: ${e.message}", e)
            false
        }
    }

    private fun isOrderStillOpen(order: ExchangeOrder, rateLimiterBehaviour: RateLimiterBehavior): Boolean {
        exchangeRateLimiter.acquireWith(rateLimiterBehaviour) { "[$exchangeName] Could not acquire permit to isOrderStillOpen" }
        val openOrders = wrapped.getOpenOrders(DefaultOpenOrdersParamCurrencyPair(order.currencyPair.toXchangeCurrencyPair()))
        logger.info { "$openOrders" }
        val orderIsOnOpenOrderList = openOrders.openOrders.any { order.orderId == it.id }
        logger.info { "${order.type} order ${order.orderId} on open order list: $orderIsOnOpenOrderList" }
        return orderIsOnOpenOrderList
    }

    private fun isOrderStillOpen(currencyPair: CurrencyPair, orderId: String, rateLimiterBehaviour: RateLimiterBehavior): Boolean {
        exchangeRateLimiter.acquireWith(rateLimiterBehaviour) { "[$exchangeName] Could not acquire permit to isOrderStillOpen by orderId" }
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
    override fun placeLimitBuyOrder(currencyPair: CurrencyPair, limitPrice: BigDecimal, amount: BigDecimal, rateLimiterBehaviour: RateLimiterBehavior): ExchangeOrder {
        val limitBuyOrder = LimitOrder.Builder(Order.OrderType.BID, currencyPair.toXchangeCurrencyPair())
            .orderStatus(Order.OrderStatus.NEW)
            .limitPrice(limitPrice)
            .originalAmount(amount)
            .build()
        logger.info { "Requesting limit buy order: $limitBuyOrder" }

        exchangeRateLimiter.acquireWith(rateLimiterBehaviour) { "[$exchangeName] Could not acquire permit to placeBuyOrder" }
        val orderId = wrapped.placeLimitOrder(limitBuyOrder)
        val receivedAtMillis = clock.millis()

        logger.info { "Limit $exchangeName-limit-buy order created with id: $orderId" }
        return ExchangeOrder(
            exchangeName = exchangeName,
            orderId = orderId,
            type = ExchangeOrderType.BID_BUY,
            orderedAmount = amount,
            filledAmount = BigDecimal.ZERO,
            price = limitPrice,
            currencyPair = currencyPair,
            status = ExchangeOrderStatus.NEW,
            receivedAtMillis = receivedAtMillis,
            exchangeTimestampMillis = null,
        )
    }

    /**
     * Sell currencyPair.base currency and gain currencyPair.counter
     */
    override fun placeLimitSellOrder(currencyPair: CurrencyPair, limitPrice: BigDecimal, amount: BigDecimal, rateLimiterBehaviour: RateLimiterBehavior): ExchangeOrder {
        val limitSellOrder = LimitOrder.Builder(Order.OrderType.ASK, currencyPair.toXchangeCurrencyPair())
            .orderStatus(Order.OrderStatus.NEW)
            .limitPrice(limitPrice)
            .originalAmount(amount)
            .build()
        logger.info { "Requesting limit sell order: $limitSellOrder" }

        exchangeRateLimiter.acquireWith(rateLimiterBehaviour) { "[$exchangeName] Could not acquire permit to placeSellOrder" }
        val orderId = wrapped.placeLimitOrder(limitSellOrder)
        val receivedAtMillis = clock.millis()

        logger.info { "Limit $exchangeName-limit-sell order created with id: $orderId" }
        return ExchangeOrder(
            exchangeName = exchangeName,
            orderId = orderId,
            type = ExchangeOrderType.ASK_SELL,
            orderedAmount = amount,
            filledAmount = BigDecimal.ZERO,
            price = limitPrice,
            currencyPair = currencyPair,
            status = ExchangeOrderStatus.NEW,
            receivedAtMillis = receivedAtMillis,
            exchangeTimestampMillis = null,
        )
    }

    override fun placeMarketBuyOrderWithBaseCurrencyAmount(
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        rateLimiterBehaviour: RateLimiterBehavior
    ): ExchangeOrder {
        val marketBuyOrder = MarketOrder.Builder(Order.OrderType.BID, currencyPair.toXchangeCurrencyPair())
            .orderStatus(Order.OrderStatus.NEW)
            .originalAmount(baseCurrencyAmount)
            .build()
        logger.info { "Requesting market buy order with base currency amount: $marketBuyOrder" }

        exchangeRateLimiter.acquireWith(rateLimiterBehaviour) { "[$exchangeName] Could not acquire permit to placeMarketBuyOrderWithBaseCurrencyAmount" }
        val orderId = wrapped.placeMarketOrder(marketBuyOrder)
        val receivedAtMillis = clock.millis()

        logger.info { "Limit $exchangeName-market-buy order created with id: $orderId" }
        return ExchangeOrder(
            exchangeName = exchangeName,
            orderId = orderId,
            type = ExchangeOrderType.BID_BUY,
            orderedAmount = baseCurrencyAmount,
            filledAmount = BigDecimal.ZERO,
            price = currentPrice,
            currencyPair = currencyPair,
            status = ExchangeOrderStatus.NEW,
            receivedAtMillis = receivedAtMillis,
            exchangeTimestampMillis = null,
        )
    }

    override fun placeMarketBuyOrderWithCounterCurrencyAmount(
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        rateLimiterBehaviour: RateLimiterBehavior
    ): ExchangeOrder {
        TODO("Not yet implemented")
    }

    override fun placeMarketSellOrderWithBaseCurrencyAmount(
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        rateLimiterBehaviour: RateLimiterBehavior
    ): ExchangeOrder {
        val marketSellOrder = MarketOrder.Builder(Order.OrderType.ASK, currencyPair.toXchangeCurrencyPair())
            .orderStatus(Order.OrderStatus.NEW)
            .originalAmount(baseCurrencyAmount)
            .build()
        logger.info { "Requesting market sell order: $marketSellOrder" }

        exchangeRateLimiter.acquireWith(rateLimiterBehaviour) { "[$exchangeName] Could not acquire permit to placeMarketSellOrderWithBaseCurrencyAmount" }
        val orderId = wrapped.placeMarketOrder(marketSellOrder)
        val receivedAtMillis = clock.millis()

        logger.info { "Limit $exchangeName-market-sell order created with id: $orderId" }
        return ExchangeOrder(
            exchangeName = exchangeName,
            orderId = orderId,
            type = ExchangeOrderType.BID_BUY,
            orderedAmount = baseCurrencyAmount,
            filledAmount = BigDecimal.ZERO,
            price = currentPrice,
            currencyPair = currencyPair,
            status = ExchangeOrderStatus.NEW,
            receivedAtMillis = receivedAtMillis,
            exchangeTimestampMillis = null,
        )
    }

    override fun placeMarketSellOrderWithCounterCurrencyAmount(
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        rateLimiterBehaviour: RateLimiterBehavior
    ): ExchangeOrder {
        TODO("Not yet implemented")
    }

}
