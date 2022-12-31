package automate.profit.autocoin.spi.exchange.order.gateway

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.order.CancelOrderParams
import automate.profit.autocoin.spi.exchange.order.Order
import automate.profit.autocoin.spi.exchange.order.service.OrderService
import java.math.BigDecimal
import java.util.*
import java.util.function.Supplier

class DelegateOrderServiceGateway(
    private val orderServiceByExchange: Map<ExchangeName, OrderService>,
) : OrderServiceGateway {
    class Builder {
        private val orderServiceByExchange = mutableMapOf<ExchangeName, OrderService>()

        fun withOrderService(orderService: OrderService): Builder {
            orderServiceByExchange[orderService.exchangeName] = orderService
            return this
        }

        fun build(): DelegateOrderServiceGateway {
            return DelegateOrderServiceGateway(Collections.unmodifiableMap(orderServiceByExchange))
        }
    }

    override fun cancelOrder(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>, cancelOrderParams: CancelOrderParams): Boolean {
        return orderServiceByExchange.getValue(exchangeName).cancelOrder(apiKey, cancelOrderParams)
    }

    override fun placeMarketBuyOrderWithCounterCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
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
        apiKey: Supplier<ApiKey>,
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
        apiKey: Supplier<ApiKey>,
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
        apiKey: Supplier<ApiKey>,
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
        apiKey: Supplier<ApiKey>,
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
        apiKey: Supplier<ApiKey>,
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
        apiKey: Supplier<ApiKey>,
    ): List<Order> {
        return orderServiceByExchange.getValue(exchangeName)
            .getOpenOrders(
                apiKey = apiKey,
            )
    }

    override fun getOpenOrders(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
    ): List<Order> {
        return orderServiceByExchange.getValue(exchangeName)
            .getOpenOrders(
                apiKey = apiKey,
                currencyPair = currencyPair,
            )
    }

}
