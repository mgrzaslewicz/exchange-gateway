package automate.profit.autocoin.spi.exchange.order.gateway.measurement

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.order.CancelOrderParams
import automate.profit.autocoin.spi.exchange.order.Order
import automate.profit.autocoin.spi.exchange.order.gateway.OrderServiceGateway
import java.math.BigDecimal
import java.time.Duration
import kotlin.system.measureTimeMillis

interface OnCancelOrderMeasured<T> {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        cancelOrderParams: CancelOrderParams,
        result: Boolean,
        duration: Duration,
    )
}

interface OnPlaceLimitBuyOrderMeasured<T> {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        buyPrice: BigDecimal,
        amount: BigDecimal,
        result: Order,
        duration: Duration,
    )
}

interface OnPlaceMarketBuyOrderWithCounterCurrencyAmount<T> {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        result: Order,
        duration: Duration,
    )
}

interface OnPlaceMarketBuyOrderWithBaseCurrencyAmount<T> {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        result: Order,
        duration: Duration,
    )
}

interface OnPlaceMarketSellOrderWithCounterCurrencyAmount<T> {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        result: Order,
        duration: Duration,
    )
}

interface OnPlaceMarketSellOrderWithBaseCurrencyAmount<T> {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
        result: Order,
        duration: Duration,
    )
}


interface OnPlaceLimitSellOrder<T> {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        sellPrice: BigDecimal,
        amount: BigDecimal,
        result: Order,
        duration: Duration,
    )
}

interface OnGetOpenOrders<T> {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        result: List<Order>,
        duration: Duration,
    )
}

interface OnGetOpenOrdersWithCurrencyPair<T> {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        result: List<Order>,
        duration: Duration,
    )
}


class MeasuringDurationOrderServiceGateway<T>(
    private val decorated: OrderServiceGateway<T>,
    private val onCancelOrderMeasuredHandlers: List<OnCancelOrderMeasured<T>> = listOf(),
    private val onPlaceLimitBuyOrderMeasured: List<OnPlaceLimitBuyOrderMeasured<T>> = listOf(),
    private val onPlaceMarketBuyOrderWithCounterCurrencyAmountHandlers: List<OnPlaceMarketBuyOrderWithCounterCurrencyAmount<T>> = listOf(),
    private val onPlaceMarketBuyOrderWithBaseCurrencyAmountHandlers: List<OnPlaceMarketBuyOrderWithBaseCurrencyAmount<T>> = listOf(),
    private val onPlaceMarketSellOrderWithCounterCurrencyAmount: List<OnPlaceMarketSellOrderWithCounterCurrencyAmount<T>> = listOf(),
    private val onPlaceMarketSellOrderWithBaseCurrencyAmount: List<OnPlaceMarketSellOrderWithBaseCurrencyAmount<T>> = listOf(),
    private val onPlaceLimitSellOrderMeasured: List<OnPlaceLimitSellOrder<T>> = listOf(),
    private val onGetOpenOrdersMeasured: List<OnGetOpenOrders<T>> = listOf(),
    private val onGetOpenOrdersWithCurrencyPairMeasured: List<OnGetOpenOrdersWithCurrencyPair<T>> = listOf(),
) : OrderServiceGateway<T> {

    override fun cancelOrder(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
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
        apiKey: ApiKeySupplier<T>,
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
        apiKey: ApiKeySupplier<T>,
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
        apiKey: ApiKeySupplier<T>,
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
        apiKey: ApiKeySupplier<T>,
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

    override fun placeLimitBuyOrder(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        buyPrice: BigDecimal,
        amount: BigDecimal,
    ): Order {
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
        apiKey: ApiKeySupplier<T>,
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
        apiKey: ApiKeySupplier<T>,
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
        apiKey: ApiKeySupplier<T>,
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
