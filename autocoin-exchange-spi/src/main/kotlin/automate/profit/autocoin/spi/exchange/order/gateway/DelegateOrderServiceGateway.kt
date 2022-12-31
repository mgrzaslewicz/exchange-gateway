package automate.profit.autocoin.spi.exchange.order.gateway

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.order.CancelOrderParams
import automate.profit.autocoin.spi.exchange.order.Order
import automate.profit.autocoin.spi.exchange.order.service.OrderService
import java.math.BigDecimal
import java.util.*

class DelegateOrderServiceGateway<T>(
    private val orderServiceByExchange: Map<ExchangeName, OrderService<T>>,
) : OrderServiceGateway<T> {
    class Builder<T> {
        private val orderServiceByExchange = mutableMapOf<ExchangeName, OrderService<T>>()

        fun withOrderService(orderService: OrderService<T>): Builder<T> {
            orderServiceByExchange[orderService.exchangeName] = orderService
            return this
        }

        fun build(): DelegateOrderServiceGateway<T> {
            return DelegateOrderServiceGateway(Collections.unmodifiableMap(orderServiceByExchange))
        }
    }

    override fun cancelOrder(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        cancelOrderParams: CancelOrderParams,
    ): Boolean {
        return orderServiceByExchange.getValue(exchangeName).cancelOrder(
            apiKey,
            cancelOrderParams,
        )
    }

    override fun placeMarketBuyOrderWithCounterCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        return orderServiceByExchange.getValue(exchangeName)
            .placeMarketBuyOrderWithCounterCurrencyAmount(
                apiKey = apiKey,
                currencyPair = currencyPair,
                counterCurrencyAmount = counterCurrencyAmount,
                currentPrice = currentPrice,
            )
    }

    override fun placeMarketBuyOrderWithBaseCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        return orderServiceByExchange.getValue(exchangeName)
            .placeMarketBuyOrderWithBaseCurrencyAmount(
                apiKey = apiKey,
                currencyPair = currencyPair,
                baseCurrencyAmount = baseCurrencyAmount,
                currentPrice = currentPrice,
            )
    }

    override fun placeMarketSellOrderWithCounterCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        return orderServiceByExchange.getValue(exchangeName)
            .placeMarketSellOrderWithCounterCurrencyAmount(
                apiKey = apiKey,
                currencyPair = currencyPair,
                counterCurrencyAmount = counterCurrencyAmount,
                currentPrice = currentPrice,
            )
    }

    override fun placeMarketSellOrderWithBaseCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        return orderServiceByExchange.getValue(exchangeName)
            .placeMarketSellOrderWithBaseCurrencyAmount(
                apiKey = apiKey,
                currencyPair = currencyPair,
                baseCurrencyAmount = baseCurrencyAmount,
                currentPrice = currentPrice,
            )
    }

    override fun placeLimitBuyOrder(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        buyPrice: BigDecimal,
        amount: BigDecimal,
    ): Order {
        return orderServiceByExchange.getValue(exchangeName)
            .placeLimitBuyOrder(
                apiKey = apiKey,
                currencyPair = currencyPair,
                buyPrice = buyPrice,
                amount = amount,
            )
    }

    override fun placeLimitSellOrder(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        sellPrice: BigDecimal,
        amount: BigDecimal,
    ): Order {
        return orderServiceByExchange.getValue(exchangeName)
            .placeLimitSellOrder(
                apiKey = apiKey,
                currencyPair = currencyPair,
                sellPrice = sellPrice,
                amount = amount,
            )
    }

    override fun getOpenOrders(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): List<Order> {
        return orderServiceByExchange.getValue(exchangeName)
            .getOpenOrders(
                apiKey = apiKey,
            )
    }

    override fun getOpenOrders(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
    ): List<Order> {
        return orderServiceByExchange.getValue(exchangeName)
            .getOpenOrders(
                apiKey = apiKey,
                currencyPair = currencyPair,
            )
    }

}
