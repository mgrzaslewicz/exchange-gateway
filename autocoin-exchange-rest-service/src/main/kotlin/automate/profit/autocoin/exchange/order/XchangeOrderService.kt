package automate.profit.autocoin.exchange.order

import automate.profit.autocoin.exchange.apikey.ExchangeKeyService
import automate.profit.autocoin.exchange.apikey.ExchangeService
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.currency.toXchangeCurrencyPair
import automate.profit.autocoin.exchange.peruser.UserExchangeServicesFactory
import automate.profit.autocoin.exchange.peruser.UserExchangeTradeService
import automate.profit.autocoin.util.toDate
import mu.KLogging
import org.knowm.xchange.dto.Order
import org.knowm.xchange.dto.trade.LimitOrder
import java.math.BigDecimal

fun ExchangeOrderType.toXchangeOrderType() = when (this) {
    ExchangeOrderType.ASK -> Order.OrderType.ASK
    ExchangeOrderType.BID -> Order.OrderType.BID
}

fun Order.OrderStatus?.toExchangeOrderStatus() = when (this) {
    Order.OrderStatus.NEW -> ExchangeOrderStatus.NEW
    Order.OrderStatus.PENDING_NEW -> ExchangeOrderStatus.NEW
    Order.OrderStatus.FILLED -> ExchangeOrderStatus.FILLED
    Order.OrderStatus.PARTIALLY_CANCELED -> ExchangeOrderStatus.PARTIALLY_FILLED
    Order.OrderStatus.CANCELED -> ExchangeOrderStatus.CANCELED
    null -> ExchangeOrderStatus.NOT_AVAILABLE
    else -> throw IllegalStateException("Status $this not handled")
}

fun Order.OrderType.toExchangeOrderType() = when (this) {
    Order.OrderType.ASK -> ExchangeOrderType.ASK
    Order.OrderType.BID -> ExchangeOrderType.BID
    else -> throw IllegalStateException("Type $this not handled")
}

fun ExchangeOrderStatus.toXchangeOrderStatus() = when (this) {
    ExchangeOrderStatus.NEW -> Order.OrderStatus.NEW
    ExchangeOrderStatus.FILLED -> Order.OrderStatus.FILLED
    ExchangeOrderStatus.PARTIALLY_FILLED -> Order.OrderStatus.PARTIALLY_FILLED
    ExchangeOrderStatus.CANCELED -> Order.OrderStatus.CANCELED
    ExchangeOrderStatus.NOT_AVAILABLE -> null
}

fun ExchangeOrder.toXchangeLimitOrder(): LimitOrder = LimitOrder.Builder(this.type.toXchangeOrderType(), this.currencyPair.toXchangeCurrencyPair())
        .id(this.orderId)
        .originalAmount(this.orderedAmount)
        .limitPrice(this.price)
        .orderStatus(this.status.toXchangeOrderStatus())
        .timestamp(this.timestamp?.toDate())
        .build()

class XchangeOrderService(private val exchangeService: ExchangeService,
                          private val exchangeKeyService: ExchangeKeyService,
                          private val userExchangeServicesFactory: UserExchangeServicesFactory) : ExchangeOrderService {

    private companion object : KLogging()

    override fun cancelOrder(exchangeName: String, exchangeUserId: String, cancelOrderParams: ExchangeCancelOrderParams): Boolean {
        logger.info("Canceling order '${cancelOrderParams.orderId}' at exchange '$exchangeName' for exchangeUser '$exchangeUserId'")
        val tradeService = getTradeService(exchangeName, exchangeUserId)
        return tradeService.cancelOrder(cancelOrderParams)
    }

    private fun getTradeService(exchangeName: String, exchangeUserId: String): UserExchangeTradeService {
        val exchangeId = exchangeService.getExchangeIdByName(exchangeName)
        val exchangeKey = exchangeKeyService.getExchangeKey(exchangeUserId, exchangeId)
                ?: throw IllegalArgumentException("Exchange key for Exchange(name=$exchangeName,id=$exchangeId) and exchangeUserId=$exchangeUserId not found")
        return userExchangeServicesFactory.createTradeService(exchangeName, exchangeKey.apiKey, exchangeKey.secretKey, exchangeKey.userName)
    }

    override fun placeLimitBuyOrder(exchangeName: String, exchangeUserId: String, baseCurrencyCode: String, counterCurrencyCode: String, buyPrice: BigDecimal, amount: BigDecimal): ExchangeOrder {
        val tradeService = getTradeService(exchangeName, exchangeUserId)
        return tradeService.placeBuyOrder(CurrencyPair(baseCurrencyCode, counterCurrencyCode), buyPrice, amount)
    }

    override fun placeLimitSellOrder(exchangeName: String, exchangeUserId: String, baseCurrencyCode: String, counterCurrencyCode: String, sellPrice: BigDecimal, amount: BigDecimal): ExchangeOrder {
        val tradeService = getTradeService(exchangeName, exchangeUserId)
        return tradeService.placeSellOrder(CurrencyPair(baseCurrencyCode, counterCurrencyCode), sellPrice, amount)
    }

    override fun getOpenOrders(exchangeName: String, exchangeUserId: String): List<ExchangeOrder> {
        val tradeService = getTradeService(exchangeName, exchangeUserId)
        return tradeService.getOpenOrders()
    }

    override fun isOrderCompleted(exchangeName: String, exchangeUserId: String, order: ExchangeOrder): Boolean {
        val tradeService = getTradeService(exchangeName, exchangeUserId)
        return tradeService.isOrderCompleted(order)
    }
}
