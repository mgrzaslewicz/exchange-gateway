package automate.profit.autocoin.exchange.order

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.SupportedExchange.*
import automate.profit.autocoin.exchange.apikey.ExchangeKeyDto
import automate.profit.autocoin.exchange.apikey.ExchangeKeyService
import automate.profit.autocoin.exchange.apikey.ExchangeService
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.currency.toXchangeCurrencyPair
import automate.profit.autocoin.exchange.peruser.UserExchangeServicesFactory
import automate.profit.autocoin.exchange.peruser.UserExchangeTradeService
import automate.profit.autocoin.exchange.wallet.ExchangeCurrencyPairsInWalletService
import automate.profit.autocoin.util.toDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
                          private val userExchangeServicesFactory: UserExchangeServicesFactory,
                          private val exchangeCurrencyPairsInWallet: ExchangeCurrencyPairsInWalletService) : ExchangeOrderService {

    private companion object : KLogging()

    override fun getOpenOrdersForAllExchangeKeys(currencyPairs: List<CurrencyPair>): List<ExchangeOpenOrders> {
        val openOrders = mutableListOf<ExchangeOpenOrders>()
        runBlocking {
            val exchangeKeysGroupedByExchangeId = getExchangeKeysGroupedByExchangeId()
            exchangeKeysGroupedByExchangeId.forEach { exchangeWithKeys ->
                val exchangeId = exchangeWithKeys.key
                val exchangeKeysWithTheSameExchange = exchangeWithKeys.value
                val exchangeName = exchangeService.getExchangeNameById(exchangeId)
                val exchangeKeysWithTheSameExchangeByUser = exchangeKeysWithTheSameExchange.groupBy { it.exchangeUserId }
                exchangeKeysWithTheSameExchangeByUser.forEach { exchangeUserId, exchangeKeys ->
                    launch(Dispatchers.IO) {
                        openOrders += tryGetOpenOrders(exchangeName, exchangeUserId, currencyPairs, exchangeKeys)
                    }
                }
            }
        }
        return openOrders
    }

    private fun tryGetOpenOrders(exchangeName: String, exchangeUserId: String, currencyPairs: List<CurrencyPair>, exchangeKeys: List<ExchangeKeyDto>): ExchangeOpenOrders {
        return try {
            val openOrdersAtExchange = getOpenOrdersFromExchange(currencyPairs, exchangeName, exchangeKeys)
            logger.info("Orders found $exchangeName: ${openOrdersAtExchange.size}")
            ExchangeOpenOrders(exchangeName = exchangeName, openOrders = openOrdersAtExchange, errorMessage = null, exchangeUserId = exchangeUserId)
        } catch (e: Exception) {
            logger.error("Could not get open orders from exchange $exchangeName and exchangeUserId $exchangeUserId", e)
            ExchangeOpenOrders(exchangeName = exchangeName, openOrders = emptyList(), errorMessage = e.message, exchangeUserId = exchangeUserId)
        }
    }

    private fun getExchangeKeysGroupedByExchangeId(): Map<String, List<ExchangeKeyDto>> {
        return exchangeKeyService.getExchangeKeys().groupBy { it.exchangeId }
    }

    private fun getOpenOrdersFromExchange(currencyPairs: List<CurrencyPair>, exchangeName: String, exchangeKeyDtos: List<ExchangeKeyDto>): List<ExchangeOrder> {
        val openOrders = mutableListOf<ExchangeOrder>()
        exchangeKeyDtos.forEach {
            val tradeService = userExchangeServicesFactory.createTradeService(exchangeName, it.apiKey, it.secretKey, it.userName)
            openOrders += getOpenOrdersFromExchange(it, tradeService, currencyPairs)
        }
        return openOrders
    }

    private fun getOpenOrdersFromExchange(exchangeKey: ExchangeKeyDto, tradeService: UserExchangeTradeService, currencyPairs: List<CurrencyPair>): List<ExchangeOrder> {
        val exchangeName = exchangeService.getExchangeNameById(exchangeKey.exchangeId)
        return when (SupportedExchange.fromExchangeName(exchangeName)) {
            BINANCE,
            KUCOIN,
            YOBIT -> { // API allows only requesting open orders per single market
                logger.debug("Requesting open orders at exchange $exchangeName for ${currencyPairs.size} markets")
                exchangeCurrencyPairsInWallet
                        .generateFromWalletIfGivenEmpty(exchangeName, exchangeKey.exchangeUserId, currencyPairs)
                        .flatMap { getOpenOrdersFromExchangeForMarket(exchangeName, tradeService, it) }
            }
            BITBAY,
            BITMEX,
            BITSTAMP,
            BITTREX,
            CRYPTOPIA,
            GATEIO,
            KRAKEN,
            POLONIEX -> {
                logger.debug("Requesting open orders at exchange $exchangeName for all markets")
                tradeService.getOpenOrders()
            }
            else -> throw IllegalArgumentException("Exchange $exchangeName not supported for getting open orders")
        }
    }

    private fun getOpenOrdersFromExchangeForMarket(exchangeName: String, tradeService: UserExchangeTradeService, currencyPair: CurrencyPair): List<ExchangeOrder> {
        logger.debug("Requesting open orders at exchange $exchangeName for market $currencyPair")
        return try {
            return tradeService.getOpenOrders(currencyPair)
        } catch (e: Exception) {
            logger.error("Could not get open $currencyPair orders from $exchangeName: ${e.message}")
            emptyList()
        }
    }

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

    override fun isOrderNotOpen(exchangeName: String, exchangeUserId: String, order: ExchangeOrder): Boolean {
        val tradeService = getTradeService(exchangeName, exchangeUserId)
        return tradeService.isOrderNotOpen(order)
    }
}
