package automate.profit.autocoin.spi.exchange.order.gateway.measurement

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.order.CancelOrderParams
import automate.profit.autocoin.spi.exchange.order.Order
import automate.profit.autocoin.spi.exchange.order.gateway.OrderServiceGateway
import java.math.BigDecimal
import java.time.Duration
import java.util.function.Supplier
import kotlin.system.measureTimeMillis

interface OnCancelOrderMeasured {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        cancelOrderParams: CancelOrderParams,
        result: Boolean,
        duration: Duration,
    )
}

interface OnPlaceLimitBuyOrderMeasured {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        buyPrice: BigDecimal,
        amount: BigDecimal,
        result: Order,
        duration: Duration,
    )
}

interface OnPlaceMarketBuyOrderWithCounterCurrencyAmount {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        result: Order,
        duration: Duration,
    )
}

interface OnPlaceMarketBuyOrderWithBaseCurrencyAmount {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        result: Order,
        duration: Duration,
    )
}

interface OnPlaceMarketSellOrderWithCounterCurrencyAmount {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        result: Order,
        duration: Duration,
    )
}

interface OnPlaceMarketSellOrderWithBaseCurrencyAmount {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        result: Order,
        duration: Duration,
    )
}


interface OnPlaceLimitSellOrder {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        sellPrice: BigDecimal,
        amount: BigDecimal,
        result: Order,
        duration: Duration,
    )
}

interface OnGetOpenOrders {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        result: List<Order>,
        duration: Duration,
    )
}

interface OnGetOpenOrdersWithCurrencyPair {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        result: List<Order>,
        duration: Duration,
    )
}


class MeasuringDurationOrderServiceGateway(
    private val decorated: OrderServiceGateway,
    private val onCancelOrderMeasuredHandlers: List<OnCancelOrderMeasured> = listOf(),
    private val onPlaceLimitBuyOrderMeasured: List<OnPlaceLimitBuyOrderMeasured> = listOf(),
    private val onPlaceMarketBuyOrderWithCounterCurrencyAmountHandlers: List<OnPlaceMarketBuyOrderWithCounterCurrencyAmount> = listOf(),
    private val onPlaceMarketBuyOrderWithBaseCurrencyAmountHandlers: List<OnPlaceMarketBuyOrderWithBaseCurrencyAmount> = listOf(),
    private val onPlaceMarketSellOrderWithCounterCurrencyAmount: List<OnPlaceMarketSellOrderWithCounterCurrencyAmount> = listOf(),
    private val onPlaceMarketSellOrderWithBaseCurrencyAmount: List<OnPlaceMarketSellOrderWithBaseCurrencyAmount> = listOf(),
    private val onPlaceLimitSellOrderMeasured: List<OnPlaceLimitSellOrder> = listOf(),
    private val onGetOpenOrdersMeasured: List<OnGetOpenOrders> = listOf(),
    private val onGetOpenOrdersWithCurrencyPairMeasured: List<OnGetOpenOrdersWithCurrencyPair> = listOf(),
) : OrderServiceGateway {

    override fun cancelOrder(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        cancelOrderParams: CancelOrderParams,
    ): Boolean {
        val result: Boolean
        val duration = Duration.ofMillis(
            measureTimeMillis {
                result = decorated.cancelOrder(
                    exchangeName = exchangeName,
                    apiKey = apiKey,
                    cancelOrderParams = cancelOrderParams,
                )
            },
        )
        onCancelOrderMeasuredHandlers.forEach {
            it(
                exchangeName = exchangeName,
                apiKey = apiKey,
                cancelOrderParams = cancelOrderParams,
                result = result,
                duration = duration,
            )
        }
        return result
    }

    override fun placeMarketBuyOrderWithCounterCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        val result: Order
        val duration = Duration.ofMillis(
            measureTimeMillis {
                result = decorated.placeMarketBuyOrderWithCounterCurrencyAmount(
                    exchangeName = exchangeName,
                    apiKey = apiKey,
                    currencyPair = currencyPair,
                    counterCurrencyAmount = counterCurrencyAmount,
                    currentPrice = currentPrice,
                )
            },
        )
        onPlaceMarketBuyOrderWithCounterCurrencyAmountHandlers.forEach {
            it(
                exchangeName = exchangeName,
                apiKey = apiKey,
                currencyPair = currencyPair,
                counterCurrencyAmount = counterCurrencyAmount,
                currentPrice = currentPrice,
                result = result,
                duration = duration,
            )
        }
        return result
    }

    override fun placeMarketBuyOrderWithBaseCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        val result: Order
        val duration = Duration.ofMillis(
            measureTimeMillis {
                result = decorated.placeMarketBuyOrderWithBaseCurrencyAmount(
                    exchangeName = exchangeName,
                    apiKey = apiKey,
                    currencyPair = currencyPair,
                    baseCurrencyAmount = baseCurrencyAmount,
                    currentPrice = currentPrice,
                )
            },
        )
        onPlaceMarketBuyOrderWithBaseCurrencyAmountHandlers.forEach {
            it(
                exchangeName = exchangeName,
                apiKey = apiKey,
                currencyPair = currencyPair,
                baseCurrencyAmount = baseCurrencyAmount,
                currentPrice = currentPrice,
                result = result,
                duration = duration,
            )
        }
        return result
    }

    override fun placeMarketSellOrderWithCounterCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        val result: Order
        val duration = Duration.ofMillis(
            measureTimeMillis {
                result = decorated.placeMarketSellOrderWithCounterCurrencyAmount(
                    exchangeName = exchangeName,
                    apiKey = apiKey,
                    currencyPair = currencyPair,
                    counterCurrencyAmount = counterCurrencyAmount,
                    currentPrice = currentPrice,
                )
            },
        )
        onPlaceMarketSellOrderWithCounterCurrencyAmount.forEach {
            it(
                exchangeName = exchangeName,
                apiKey = apiKey,
                currencyPair = currencyPair,
                counterCurrencyAmount = counterCurrencyAmount,
                currentPrice = currentPrice,
                result = result,
                duration = duration,
            )
        }
        return result
    }

    override fun placeMarketSellOrderWithBaseCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        val result: Order
        val duration = Duration.ofMillis(
            measureTimeMillis {
                result = decorated.placeMarketSellOrderWithBaseCurrencyAmount(
                    exchangeName = exchangeName,
                    apiKey = apiKey,
                    currencyPair = currencyPair,
                    baseCurrencyAmount = baseCurrencyAmount,
                    currentPrice = currentPrice,
                )
            },
        )
        onPlaceMarketSellOrderWithBaseCurrencyAmount.forEach {
            it(
                exchangeName = exchangeName,
                apiKey = apiKey,
                currencyPair = currencyPair,
                baseCurrencyAmount = baseCurrencyAmount,
                currentPrice = currentPrice,
                result = result,
                duration = duration,
            )
        }
        return result
    }

    override fun placeLimitBuyOrder(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>, currencyPair: CurrencyPair, buyPrice: BigDecimal, amount: BigDecimal): Order {
        val result: Order
        val duration = Duration.ofMillis(
            measureTimeMillis {
                result = decorated.placeLimitBuyOrder(
                    exchangeName = exchangeName,
                    apiKey = apiKey,
                    currencyPair = currencyPair,
                    buyPrice = buyPrice,
                    amount = amount,
                )
            },
        )
        onPlaceLimitBuyOrderMeasured.forEach {
            it(
                exchangeName = exchangeName,
                apiKey = apiKey,
                currencyPair = currencyPair,
                buyPrice = buyPrice,
                amount = amount,
                result = result,
                duration = duration,
            )
        }
        return result
    }

    override fun placeLimitSellOrder(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        sellPrice: BigDecimal,
        amount: BigDecimal,
    ): Order {
        val result: Order
        val duration = Duration.ofMillis(
            measureTimeMillis {
                result = decorated.placeLimitSellOrder(
                    exchangeName = exchangeName,
                    apiKey = apiKey,
                    currencyPair = currencyPair,
                    sellPrice = sellPrice,
                    amount = amount,
                )
            },
        )
        onPlaceLimitSellOrderMeasured.forEach {
            it(
                exchangeName = exchangeName,
                apiKey = apiKey,
                currencyPair = currencyPair,
                sellPrice = sellPrice,
                amount = amount,
                result = result,
                duration = duration,
            )
        }
        return result
    }

    override fun getOpenOrders(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
    ): List<Order> {
        val result: List<Order>
        val duration = Duration.ofMillis(
            measureTimeMillis {
                result = decorated.getOpenOrders(
                    exchangeName = exchangeName,
                    apiKey = apiKey,
                )
            },
        )
        onGetOpenOrdersMeasured.forEach {
            it(
                exchangeName = exchangeName,
                apiKey = apiKey,
                result = result,
                duration = duration,
            )
        }
        return result
    }

    override fun getOpenOrders(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
    ): List<Order> {
        val result: List<Order>
        val duration = Duration.ofMillis(
            measureTimeMillis {
                result = decorated.getOpenOrders(
                    exchangeName = exchangeName,
                    apiKey = apiKey,
                    currencyPair = currencyPair,
                )
            },
        )
        onGetOpenOrdersWithCurrencyPairMeasured.forEach {
            it(
                exchangeName = exchangeName,
                apiKey = apiKey,
                currencyPair = currencyPair,
                result = result,
                duration = duration,
            )
        }
        return result
    }
}
