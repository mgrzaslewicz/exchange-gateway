package automate.profit.autocoin.exchange.order

import automate.profit.autocoin.exchange.currency.CurrencyPair
import mu.KLogging
import org.knowm.xchange.dto.Order
import java.math.BigDecimal
import java.time.Instant

enum class ExchangeOrderType {
    ASK,
    BID
}

enum class ExchangeOrderStatus {
    NEW,
    FILLED,
    PARTIALLY_FILLED,
    CANCELED,
    NOT_AVAILABLE
}

data class ExchangeCancelOrderParams(
        val orderId: String,
        val orderType: ExchangeOrderType,
        val currencyPair: CurrencyPair
) {
    fun xchangeOrderType(): Order.OrderType = when (this.orderType) {
        ExchangeOrderType.ASK -> Order.OrderType.ASK
        ExchangeOrderType.BID -> Order.OrderType.BID
    }
}

data class ExchangeOrder(
        val exchangeName: String,
        /** id at external exchange **/
        val orderId: String,
        val type: ExchangeOrderType,
        val orderedAmount: BigDecimal,
        val filledAmount: BigDecimal?,
        val price: BigDecimal,
        val currencyPair: CurrencyPair,
        val status: ExchangeOrderStatus,
        val timestamp: Instant?
)

data class ExchangeOpenOrders(
        val exchangeName: String,
        val exchangeUserId: String,
        val openOrders: List<ExchangeOrder>,
        val errorMessage: String?
)

interface ExchangeOrderService {
    private companion object : KLogging()

    fun cancelOrder(exchangeName: String, exchangeUserId: String, cancelOrderParams: ExchangeCancelOrderParams): Boolean

    fun placeLimitBuyOrder(exchangeName: String, exchangeUserId: String, baseCurrencyCode: String, counterCurrencyCode: String, buyPrice: BigDecimal, amount: BigDecimal): ExchangeOrder

    fun placeLimitSellOrder(exchangeName: String, exchangeUserId: String, baseCurrencyCode: String, counterCurrencyCode: String, sellPrice: BigDecimal, amount: BigDecimal): ExchangeOrder

    fun getOpenOrders(exchangeName: String, exchangeUserId: String): List<ExchangeOrder>

    fun getOpenOrdersForAllExchangeKeys(): List<ExchangeOpenOrders> = TODO("Not implemented yet")

    fun isOrderCompleted(exchangeName: String, exchangeUserId: String, order: ExchangeOrder): Boolean

}
