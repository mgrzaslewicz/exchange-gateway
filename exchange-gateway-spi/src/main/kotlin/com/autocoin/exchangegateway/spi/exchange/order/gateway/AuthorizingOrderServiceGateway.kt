package com.autocoin.exchangegateway.spi.exchange.order.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.CancelOrderParams
import com.autocoin.exchangegateway.spi.exchange.order.Order
import com.autocoin.exchangegateway.spi.exchange.order.service.authorized.AuthorizedOrderServiceFactory
import java.math.BigDecimal

class AuthorizingOrderServiceGateway<T>(
    private val authorizedOrderServiceFactory: AuthorizedOrderServiceFactory<T>,
) : OrderServiceGateway<T> {

    override fun cancelOrder(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        cancelOrderParams: CancelOrderParams,
    ): Boolean {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(
                exchange = exchange,
                apiKey = apiKey,
            )
            .cancelOrder(cancelOrderParams)
    }

    override fun placeMarketBuyOrderWithCounterCurrencyAmount(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(
                exchange = exchange,
                apiKey = apiKey,
            )
            .placeMarketBuyOrderWithCounterCurrencyAmount(
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
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(
                exchange = exchange,
                apiKey = apiKey,
            )
            .placeMarketBuyOrderWithBaseCurrencyAmount(
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
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(
                exchange = exchange,
                apiKey = apiKey,
            )
            .placeMarketSellOrderWithCounterCurrencyAmount(
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
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(
                exchange = exchange,
                apiKey = apiKey,
            )
            .placeMarketSellOrderWithBaseCurrencyAmount(
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
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(
                exchange = exchange,
                apiKey = apiKey,
            )
            .placeLimitBuyOrder(
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
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(
                exchange = exchange,
                apiKey = apiKey,
            )
            .placeLimitSellOrder(
                currencyPair = currencyPair,
                sellPrice = sellPrice,
                amount = amount,
            )
    }

    override fun getOpenOrders(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): List<Order> {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(
                exchange = exchange,
                apiKey = apiKey,
            )
            .getOpenOrders()
    }

    override fun getOpenOrders(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
    ): List<Order> {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(
                exchange = exchange,
                apiKey = apiKey,
            )
            .getOpenOrders(currencyPair = currencyPair)
    }
}
