package com.autocoin.exchangegateway.spi.exchange.order.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.CancelOrderParams
import com.autocoin.exchangegateway.spi.exchange.order.Order
import com.autocoin.exchangegateway.spi.exchange.order.service.OrderService
import java.math.BigDecimal
import java.util.*

class DelegateOrderServiceGateway<T>(
    private val orderServiceByExchange: Map<Exchange, OrderService<T>>,
) : OrderServiceGateway<T> {
    class Builder<T> {
        private val orderServiceByExchange = mutableMapOf<Exchange, OrderService<T>>()

        fun withOrderService(orderService: OrderService<T>): Builder<T> {
            orderServiceByExchange[orderService.exchange] = orderService
            return this
        }

        fun build(): DelegateOrderServiceGateway<T> {
            return DelegateOrderServiceGateway(Collections.unmodifiableMap(orderServiceByExchange))
        }
    }

    override fun cancelOrder(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        cancelOrderParams: CancelOrderParams,
    ): Boolean {
        return orderServiceByExchange.getValue(exchange).cancelOrder(
            apiKey,
            cancelOrderParams,
        )
    }

    override fun placeMarketBuyOrderWithCounterCurrencyAmount(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        return orderServiceByExchange.getValue(exchange)
            .placeMarketBuyOrderWithCounterCurrencyAmount(
                apiKey = apiKey,
                currencyPair = currencyPair,
                counterCurrencyAmount = counterCurrencyAmount,
                currentPrice = currentPrice,
            )
    }

    override fun placeMarketBuyOrderWithBaseCurrencyAmount(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        return orderServiceByExchange.getValue(exchange)
            .placeMarketBuyOrderWithBaseCurrencyAmount(
                apiKey = apiKey,
                currencyPair = currencyPair,
                baseCurrencyAmount = baseCurrencyAmount,
                currentPrice = currentPrice,
            )
    }

    override fun placeMarketSellOrderWithCounterCurrencyAmount(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        return orderServiceByExchange.getValue(exchange)
            .placeMarketSellOrderWithCounterCurrencyAmount(
                apiKey = apiKey,
                currencyPair = currencyPair,
                counterCurrencyAmount = counterCurrencyAmount,
                currentPrice = currentPrice,
            )
    }

    override fun placeMarketSellOrderWithBaseCurrencyAmount(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        return orderServiceByExchange.getValue(exchange)
            .placeMarketSellOrderWithBaseCurrencyAmount(
                apiKey = apiKey,
                currencyPair = currencyPair,
                baseCurrencyAmount = baseCurrencyAmount,
                currentPrice = currentPrice,
            )
    }

    override fun placeLimitBuyOrder(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        buyPrice: BigDecimal,
        amount: BigDecimal,
    ): Order {
        return orderServiceByExchange.getValue(exchange)
            .placeLimitBuyOrder(
                apiKey = apiKey,
                currencyPair = currencyPair,
                buyPrice = buyPrice,
                amount = amount,
            )
    }

    override fun placeLimitSellOrder(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        sellPrice: BigDecimal,
        amount: BigDecimal,
    ): Order {
        return orderServiceByExchange.getValue(exchange)
            .placeLimitSellOrder(
                apiKey = apiKey,
                currencyPair = currencyPair,
                sellPrice = sellPrice,
                amount = amount,
            )
    }

    override fun getOpenOrders(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): List<Order> {
        return orderServiceByExchange.getValue(exchange)
            .getOpenOrders(
                apiKey = apiKey,
            )
    }

    override fun getOpenOrders(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
    ): List<Order> {
        return orderServiceByExchange.getValue(exchange)
            .getOpenOrders(
                apiKey = apiKey,
                currencyPair = currencyPair,
            )
    }

}
