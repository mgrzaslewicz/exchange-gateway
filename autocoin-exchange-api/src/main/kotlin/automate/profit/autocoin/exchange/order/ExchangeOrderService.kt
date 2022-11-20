package automate.profit.autocoin.exchange.order

import automate.profit.autocoin.exchange.apikey.ExchangeKeyDto
import automate.profit.autocoin.exchange.currency.CurrencyPair
import java.math.BigDecimal

enum class ExchangeOrderType {
    ASK_SELL,
    BID_BUY
}

enum class ExchangeOrderStatus {
    NEW,
    FILLED,
    PARTIALLY_FILLED,
    PARTIALLY_CANCELED,
    CANCELED,
    NOT_AVAILABLE
}

/**
 * Having just orderId is not enough.
 * Some exchanges require more parameters when canceling the order
 */
data class ExchangeCancelOrderParams(
    val orderId: String,
    val orderType: ExchangeOrderType,
    val currencyPair: CurrencyPair
)

data class ExchangeOpenOrders(
    val exchangeName: String,
    val exchangeUserId: String,
    val openOrders: List<ExchangeOrder>,
    val errorMessage: String?
)

interface ExchangeOrderService {

    fun cancelOrder(exchangeName: String, exchangeUserId: String, cancelOrderParams: ExchangeCancelOrderParams): Boolean
    fun cancelOrder(exchangeName: String, exchangeKey: ExchangeKeyDto, cancelOrderParams: ExchangeCancelOrderParams): Boolean

    fun placeLimitBuyOrder(
        exchangeName: String,
        exchangeUserId: String,
        baseCurrencyCode: String,
        counterCurrencyCode: String,
        buyPrice: BigDecimal,
        amount: BigDecimal,
        isDemoOrder: Boolean = false
    ): ExchangeOrder

    fun placeMarketBuyOrderWithCounterCurrencyAmount(
        exchangeName: String,
        exchangeKey: ExchangeKeyDto,
        baseCurrencyCode: String,
        counterCurrencyCode: String,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        isDemoOrder: Boolean = false
    ): ExchangeOrder

    fun placeMarketBuyOrderWithBaseCurrencyAmount(
        exchangeName: String,
        exchangeKey: ExchangeKeyDto,
        baseCurrencyCode: String,
        counterCurrencyCode: String,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        isDemoOrder: Boolean = false
    ): ExchangeOrder

    fun placeMarketSellOrderWithCounterCurrencyAmount(
        exchangeName: String,
        exchangeKey: ExchangeKeyDto,
        baseCurrencyCode: String,
        counterCurrencyCode: String,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        isDemoOrder: Boolean = false
    ): ExchangeOrder

    fun placeMarketSellOrderWithBaseCurrencyAmount(
        exchangeName: String,
        exchangeKey: ExchangeKeyDto,
        baseCurrencyCode: String,
        counterCurrencyCode: String,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        isDemoOrder: Boolean = false
    ): ExchangeOrder

    fun placeLimitBuyOrder(
        exchangeName: String,
        exchangeKey: ExchangeKeyDto,
        baseCurrencyCode: String,
        counterCurrencyCode: String,
        buyPrice: BigDecimal,
        amount: BigDecimal,
        isDemoOrder: Boolean = false
    ): ExchangeOrder

    fun placeLimitSellOrder(
        exchangeName: String,
        exchangeUserId: String,
        baseCurrencyCode: String,
        counterCurrencyCode: String,
        sellPrice: BigDecimal,
        amount: BigDecimal,
        isDemoOrder: Boolean = false
    ): ExchangeOrder

    fun placeLimitSellOrder(
        exchangeName: String,
        exchangeKey: ExchangeKeyDto,
        baseCurrencyCode: String,
        counterCurrencyCode: String,
        sellPrice: BigDecimal,
        amount: BigDecimal,
        isDemoOrder: Boolean = false
    ): ExchangeOrder

    fun getOpenOrders(exchangeName: String, exchangeUserId: String): List<ExchangeOrder>

    fun getOpenOrders(exchangeName: String, exchangeKey: ExchangeKeyDto): List<ExchangeOrder>


    fun getOpenOrdersForAllExchangeKeys(): List<ExchangeOpenOrders> = getOpenOrdersForAllExchangeKeys(emptyList())

    fun getOpenOrdersForAllExchangeKeys(currencyPairs: List<CurrencyPair>): List<ExchangeOpenOrders>

    fun isOrderNotOpen(exchangeName: String, exchangeUserId: String, order: ExchangeOrder): Boolean

}
