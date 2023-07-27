package com.autocoin.exchangegateway.spi.exchange.order.service

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.CancelOrderParams
import com.autocoin.exchangegateway.spi.exchange.order.Order
import com.autocoin.exchangegateway.spi.exchange.order.service.authorized.AuthorizedOrderServiceFactory
import java.math.BigDecimal

class OrderServiceUsingAuthorizedOrderService<T>(
    override val exchange: Exchange,
    private val authorizedOrderServiceFactory: AuthorizedOrderServiceFactory<T>,
) : OrderService<T> {
    override fun cancelOrder(
        apiKey: ApiKeySupplier<T>,
        cancelOrderParams: CancelOrderParams,
    ): Boolean {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(apiKey = apiKey, exchange = exchange)
            .cancelOrder(cancelOrderParams)
    }

    override fun placeMarketBuyOrderWithCounterCurrencyAmount(
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(apiKey = apiKey, exchange = exchange)
            .placeMarketBuyOrderWithCounterCurrencyAmount(
                currencyPair = currencyPair,
                counterCurrencyAmount = counterCurrencyAmount,
                currentPrice = currentPrice,
            )
    }

    override fun placeMarketBuyOrderWithBaseCurrencyAmount(
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(apiKey = apiKey, exchange = exchange)
            .placeMarketBuyOrderWithBaseCurrencyAmount(
                currencyPair = currencyPair,
                baseCurrencyAmount = baseCurrencyAmount,
                currentPrice = currentPrice,
            )
    }

    override fun placeMarketSellOrderWithCounterCurrencyAmount(
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(apiKey = apiKey, exchange = exchange)
            .placeMarketSellOrderWithCounterCurrencyAmount(
                currencyPair = currencyPair,
                counterCurrencyAmount = counterCurrencyAmount,
                currentPrice = currentPrice,
            )
    }

    override fun placeMarketSellOrderWithBaseCurrencyAmount(
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(apiKey = apiKey, exchange = exchange)
            .placeMarketSellOrderWithBaseCurrencyAmount(
                currencyPair = currencyPair,
                baseCurrencyAmount = baseCurrencyAmount,
                currentPrice = currentPrice,
            )
    }

    override fun placeLimitBuyOrder(
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        buyPrice: BigDecimal,
        amount: BigDecimal,
    ): Order {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(apiKey = apiKey, exchange = exchange)
            .placeLimitBuyOrder(
                currencyPair = currencyPair,
                buyPrice = buyPrice,
                amount = amount,
            )
    }

    override fun placeLimitSellOrder(
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        sellPrice: BigDecimal,
        amount: BigDecimal,
    ): Order {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(apiKey = apiKey, exchange = exchange)
            .placeLimitSellOrder(
                currencyPair = currencyPair,
                sellPrice = sellPrice,
                amount = amount,
            )
    }

    override fun getOpenOrders(apiKey: ApiKeySupplier<T>): List<Order> {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(apiKey = apiKey, exchange = exchange)
            .getOpenOrders()
    }

    override fun getOpenOrders(
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
    ): List<Order> {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(
                apiKey = apiKey,
                exchange = exchange,
            )
            .getOpenOrders(currencyPair = currencyPair)
    }
}
