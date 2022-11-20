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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.knowm.xchange.dto.Order
import org.knowm.xchange.dto.trade.LimitOrder
import java.math.BigDecimal
import java.util.*

private fun Long.toDate() = Date(this)

fun ExchangeOrderType.toXchangeOrderType() = when (this) {
    ExchangeOrderType.ASK_SELL -> Order.OrderType.ASK
    ExchangeOrderType.BID_BUY -> Order.OrderType.BID
}

fun Order.OrderStatus?.toExchangeOrderStatus() = when (this) {
    Order.OrderStatus.NEW -> ExchangeOrderStatus.NEW
    Order.OrderStatus.PENDING_NEW -> ExchangeOrderStatus.NEW
    Order.OrderStatus.FILLED -> ExchangeOrderStatus.FILLED
    Order.OrderStatus.PARTIALLY_CANCELED -> ExchangeOrderStatus.PARTIALLY_CANCELED
    Order.OrderStatus.PARTIALLY_FILLED -> ExchangeOrderStatus.PARTIALLY_FILLED
    Order.OrderStatus.CANCELED -> ExchangeOrderStatus.CANCELED
    null -> ExchangeOrderStatus.NOT_AVAILABLE
    else -> throw IllegalStateException("Status $this not handled")
}

fun Order.OrderType.toExchangeOrderType() = when (this) {
    Order.OrderType.ASK -> ExchangeOrderType.ASK_SELL
    Order.OrderType.BID -> ExchangeOrderType.BID_BUY
    else -> throw IllegalStateException("Type $this not handled")
}

fun ExchangeOrderStatus.toXchangeOrderStatus() = when (this) {
    ExchangeOrderStatus.NEW -> Order.OrderStatus.NEW
    ExchangeOrderStatus.FILLED -> Order.OrderStatus.FILLED
    ExchangeOrderStatus.PARTIALLY_FILLED -> Order.OrderStatus.PARTIALLY_FILLED
    ExchangeOrderStatus.PARTIALLY_CANCELED -> Order.OrderStatus.PARTIALLY_CANCELED
    ExchangeOrderStatus.CANCELED -> Order.OrderStatus.CANCELED
    ExchangeOrderStatus.NOT_AVAILABLE -> null
}

fun ExchangeOrder.toXchangeLimitOrder(): LimitOrder = LimitOrder.Builder(this.type.toXchangeOrderType(), this.currencyPair.toXchangeCurrencyPair())
    .id(this.orderId)
    .originalAmount(this.orderedAmount)
    .limitPrice(this.price)
    .orderStatus(this.status.toXchangeOrderStatus())
    .timestamp(this.exchangeTimestampMillis?.toDate())
    .build()

class XchangeOrderService(
    private val exchangeService: ExchangeService,
    private val exchangeKeyService: ExchangeKeyService,
    private val userExchangeServicesFactory: UserExchangeServicesFactory,
    private val exchangeCurrencyPairsInWallet: ExchangeCurrencyPairsInWalletService,
    private val demoOrderCreator: DemoOrderCreator
) : ExchangeOrderService {

    private companion object : KLogging()

    override fun getOpenOrdersForAllExchangeKeys(currencyPairs: List<CurrencyPair>): List<ExchangeOpenOrders> {
        val openOrders = mutableListOf<ExchangeOpenOrders>()
        // use exchangeService and exchangeKeyService before creating new threads to avoid problems with scoped beans in spring
        val exchangeKeysGroupedByExchangeId = getExchangeKeysGroupedByExchangeId()
        val exchangeId2Name = exchangeKeysGroupedByExchangeId.keys.map {
            it to exchangeService.getExchangeNameById(it)
        }.toMap()
        runBlocking {
            exchangeKeysGroupedByExchangeId.forEach { exchangeIdToKeys ->
                launch(Dispatchers.IO) {
                    val exchangeId = exchangeIdToKeys.key
                    val exchangeKeys = exchangeIdToKeys.value
                    val exchangeName = exchangeId2Name.getValue(exchangeId)
                    val exchangeKeysGroupedByByUser = exchangeKeys.groupBy { it.exchangeUserId }
                    exchangeKeysGroupedByByUser.forEach { (exchangeUserId, exchangeKeys) ->
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

    private fun getOpenOrdersFromExchange(currencyPairs: List<CurrencyPair>, exchangeName: String, exchangeKeys: List<ExchangeKeyDto>): List<ExchangeOrder> {
        return exchangeKeys.flatMap {
            val tradeService = userExchangeServicesFactory.createTradeService(exchangeName, it.apiKey, it.secretKey, it.userName, it.exchangeSpecificKeyParameters)
            getOpenOrdersFromExchange(exchangeName, it, tradeService, currencyPairs)
        }
    }

    private fun getOpenOrdersFromExchange(
        exchangeName: String,
        exchangeKey: ExchangeKeyDto,
        tradeService: UserExchangeTradeService,
        currencyPairs: List<CurrencyPair>
    ): List<ExchangeOrder> {
        return when (SupportedExchange.fromExchangeName(exchangeName)) {
            YOBIT -> { // API allows only requesting open orders per single market
                logger.debug("Requesting open orders at exchange $exchangeName for markets:  $currencyPairs")
                exchangeCurrencyPairsInWallet
                    .generateFromWalletIfGivenEmpty(exchangeName, exchangeKey, currencyPairs)
                    .flatMap { getOpenOrdersFromExchangeForMarket(exchangeName, tradeService, it) }
            }

            BINANCE,
            BITBAY,
            BITMEX,
            BITSTAMP,
            BITTREX,
            GATEIO,
            KRAKEN,
            KUCOIN,
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

    override fun cancelOrder(exchangeName: String, exchangeKey: ExchangeKeyDto, cancelOrderParams: ExchangeCancelOrderParams): Boolean {
        logger.info("Canceling order '${cancelOrderParams.orderId}' at exchange '$exchangeName' for exchangeUser '${exchangeKey.exchangeUserId}'")
        val tradeService = getTradeService(exchangeName, exchangeKey)
        return tradeService.cancelOrder(cancelOrderParams)
    }

    private fun getTradeService(exchangeName: String, exchangeUserId: String): UserExchangeTradeService {
        val exchangeId = exchangeService.getExchangeIdByName(exchangeName)
        val exchangeKey = exchangeKeyService.getExchangeKey(exchangeUserId, exchangeId)
            ?: throw IllegalArgumentException("Exchange key for Exchange(name=$exchangeName,id=$exchangeId) and exchangeUserId=$exchangeUserId not found")
        return getTradeService(exchangeName, exchangeKey)
    }

    fun getTradeService(exchangeName: String, exchangeKey: ExchangeKeyDto): UserExchangeTradeService {
        return userExchangeServicesFactory.createTradeService(
            exchangeName = exchangeName,
            publicKey = exchangeKey.apiKey,
            secretKey = exchangeKey.secretKey,
            userName = exchangeKey.userName,
            exchangeSpecificKeyParameters = exchangeKey.exchangeSpecificKeyParameters
        )
    }

    override fun placeLimitBuyOrder(
        exchangeName: String,
        exchangeUserId: String,
        baseCurrencyCode: String,
        counterCurrencyCode: String,
        buyPrice: BigDecimal,
        amount: BigDecimal,
        isDemoOrder: Boolean
    ): ExchangeOrder {
        return if (isDemoOrder) {
            demoOrderCreator.placeLimitBuyOrder(
                exchangeName = exchangeName,
                exchangeUserId = exchangeUserId,
                baseCurrencyCode = baseCurrencyCode,
                counterCurrencyCode = counterCurrencyCode,
                buyPrice = buyPrice,
                amount = amount
            )
        } else {
            val tradeService = getTradeService(exchangeName, exchangeUserId)
            tradeService.placeLimitBuyOrder(CurrencyPair.of(baseCurrencyCode, counterCurrencyCode), buyPrice, amount)
        }
    }

    override fun placeMarketBuyOrderWithBaseCurrencyAmount(
        exchangeName: String,
        exchangeKey: ExchangeKeyDto,
        baseCurrencyCode: String,
        counterCurrencyCode: String,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        isDemoOrder: Boolean
    ): ExchangeOrder {
        return if (isDemoOrder) {
            demoOrderCreator.placeMarketBuyOrder(
                exchangeName = exchangeName,
                exchangeUserId = exchangeKey.exchangeUserId,
                baseCurrencyCode = baseCurrencyCode,
                counterCurrencyCode = counterCurrencyCode,
                baseCurrencyAmount = baseCurrencyAmount,
                counterCurrencyAmount = null,
                currentPrice = currentPrice,
            )
        } else {
            val tradeService = getTradeService(exchangeName, exchangeKey)
            tradeService.placeMarketBuyOrderWithBaseCurrencyAmount(
                currencyPair = CurrencyPair.of(baseCurrencyCode, counterCurrencyCode),
                baseCurrencyAmount = baseCurrencyAmount,
                currentPrice = currentPrice,
            )
        }
    }

    override fun placeMarketBuyOrderWithCounterCurrencyAmount(
        exchangeName: String,
        exchangeKey: ExchangeKeyDto,
        baseCurrencyCode: String,
        counterCurrencyCode: String,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        isDemoOrder: Boolean
    ): ExchangeOrder {
        return if (isDemoOrder) {
            demoOrderCreator.placeMarketBuyOrder(
                exchangeName = exchangeName,
                exchangeUserId = exchangeKey.exchangeUserId,
                baseCurrencyCode = baseCurrencyCode,
                counterCurrencyCode = counterCurrencyCode,
                baseCurrencyAmount = null,
                counterCurrencyAmount = counterCurrencyAmount,
                currentPrice = currentPrice,
            )
        } else {
            val tradeService = getTradeService(exchangeName, exchangeKey)
            tradeService.placeMarketBuyOrderWithCounterCurrencyAmount(
                currencyPair = CurrencyPair.of(baseCurrencyCode, counterCurrencyCode),
                counterCurrencyAmount = counterCurrencyAmount,
                currentPrice = currentPrice,
            )
        }
    }

    override fun placeMarketSellOrderWithBaseCurrencyAmount(
        exchangeName: String,
        exchangeKey: ExchangeKeyDto,
        baseCurrencyCode: String,
        counterCurrencyCode: String,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        isDemoOrder: Boolean
    ): ExchangeOrder {
        return if (isDemoOrder) {
            demoOrderCreator.placeMarketSellOrder(
                exchangeName = exchangeName,
                exchangeUserId = exchangeKey.exchangeUserId,
                baseCurrencyCode = baseCurrencyCode,
                counterCurrencyCode = counterCurrencyCode,
                baseCurrencyAmount = baseCurrencyAmount,
                counterCurrencyAmount = null,
                currentPrice = currentPrice,
            )
        } else {
            val tradeService = getTradeService(exchangeName, exchangeKey)
            tradeService.placeMarketSellOrderWithBaseCurrencyAmount(
                currencyPair = CurrencyPair.of(baseCurrencyCode, counterCurrencyCode),
                baseCurrencyAmount = baseCurrencyAmount,
                currentPrice = currentPrice,
            )
        }
    }

    override fun placeMarketSellOrderWithCounterCurrencyAmount(
        exchangeName: String,
        exchangeKey: ExchangeKeyDto,
        baseCurrencyCode: String,
        counterCurrencyCode: String,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        isDemoOrder: Boolean
    ): ExchangeOrder {
        return if (isDemoOrder) {
            demoOrderCreator.placeMarketSellOrder(
                exchangeName = exchangeName,
                exchangeUserId = exchangeKey.exchangeUserId,
                baseCurrencyCode = baseCurrencyCode,
                counterCurrencyCode = counterCurrencyCode,
                baseCurrencyAmount = null,
                counterCurrencyAmount = counterCurrencyAmount,
                currentPrice = currentPrice,
            )
        } else {
            val tradeService = getTradeService(exchangeName, exchangeKey)
            tradeService.placeMarketSellOrderWithCounterCurrencyAmount(
                currencyPair = CurrencyPair.of(baseCurrencyCode, counterCurrencyCode),
                counterCurrencyAmount = counterCurrencyAmount,
                currentPrice = currentPrice,
            )
        }
    }


    override fun placeLimitBuyOrder(
        exchangeName: String,
        exchangeKey: ExchangeKeyDto,
        baseCurrencyCode: String,
        counterCurrencyCode: String,
        buyPrice: BigDecimal,
        amount: BigDecimal,
        isDemoOrder: Boolean,
    ): ExchangeOrder {
        return if (isDemoOrder) {
            demoOrderCreator.placeLimitBuyOrder(exchangeName, exchangeKey.exchangeUserId, baseCurrencyCode, counterCurrencyCode, buyPrice, amount)
        } else {
            val tradeService = getTradeService(exchangeName, exchangeKey)
            tradeService.placeLimitBuyOrder(CurrencyPair.of(base = baseCurrencyCode, counter = counterCurrencyCode), buyPrice, amount)
        }
    }

    override fun placeLimitSellOrder(
        exchangeName: String,
        exchangeUserId: String,
        baseCurrencyCode: String,
        counterCurrencyCode: String,
        sellPrice: BigDecimal,
        amount: BigDecimal,
        isDemoOrder: Boolean
    ): ExchangeOrder {
        return if (isDemoOrder) {
            demoOrderCreator.placeLimitSellOrder(exchangeName, exchangeUserId, baseCurrencyCode, counterCurrencyCode, sellPrice, amount)
        } else {
            val tradeService = getTradeService(exchangeName, exchangeUserId)
            return tradeService.placeLimitSellOrder(CurrencyPair.of(base = baseCurrencyCode, counter = counterCurrencyCode), sellPrice, amount)
        }
    }

    override fun placeLimitSellOrder(
        exchangeName: String,
        exchangeKey: ExchangeKeyDto,
        baseCurrencyCode: String,
        counterCurrencyCode: String,
        sellPrice: BigDecimal,
        amount: BigDecimal,
        isDemoOrder: Boolean,
    ): ExchangeOrder {
        return if (isDemoOrder) {
            demoOrderCreator.placeLimitSellOrder(exchangeName, exchangeKey.exchangeUserId, baseCurrencyCode, counterCurrencyCode, sellPrice, amount)
        } else {
            val tradeService = getTradeService(exchangeName, exchangeKey)
            tradeService.placeLimitSellOrder(CurrencyPair.of(base = baseCurrencyCode, counter = counterCurrencyCode), sellPrice, amount)
        }
    }

    override fun getOpenOrders(exchangeName: String, exchangeUserId: String): List<ExchangeOrder> {
        val tradeService = getTradeService(exchangeName, exchangeUserId)
        return tradeService.getOpenOrders()
    }

    override fun getOpenOrders(exchangeName: String, exchangeKey: ExchangeKeyDto): List<ExchangeOrder> {
        val tradeService = getTradeService(exchangeName, exchangeKey)
        return tradeService.getOpenOrders()
    }

    override fun isOrderNotOpen(exchangeName: String, exchangeUserId: String, order: ExchangeOrder): Boolean {
        val tradeService = getTradeService(exchangeName, exchangeUserId)
        return tradeService.isOrderNotOpen(order)
    }
}
