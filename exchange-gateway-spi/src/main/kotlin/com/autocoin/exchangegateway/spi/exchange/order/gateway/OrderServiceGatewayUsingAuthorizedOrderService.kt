package com.autocoin.exchangegateway.spi.exchange.order.gateway

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.CancelOrderParams
import com.autocoin.exchangegateway.spi.exchange.order.Order
import com.autocoin.exchangegateway.spi.exchange.order.service.authorized.AuthorizedOrderServiceFactory
import java.math.BigDecimal

class OrderServiceGatewayUsingAuthorizedOrderService<T>(
    private val authorizedOrderServiceFactory: AuthorizedOrderServiceFactory<T>,
) : OrderServiceGateway<T> {

    override fun cancelOrder(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        cancelOrderParams: CancelOrderParams,
    ): Boolean {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(
                exchangeName = exchangeName,
                apiKey = apiKey,
            )
            .cancelOrder(cancelOrderParams)
    }

    override fun placeMarketBuyOrderWithCounterCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(
                exchangeName = exchangeName,
                apiKey = apiKey,
            )
            .placeMarketBuyOrderWithCounterCurrencyAmount(
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
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(
                exchangeName = exchangeName,
                apiKey = apiKey,
            )
            .placeMarketBuyOrderWithBaseCurrencyAmount(
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
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(
                exchangeName = exchangeName,
                apiKey = apiKey,
            )
            .placeMarketSellOrderWithCounterCurrencyAmount(
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
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(
                exchangeName = exchangeName,
                apiKey = apiKey,
            )
            .placeMarketSellOrderWithBaseCurrencyAmount(
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
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(
                exchangeName = exchangeName,
                apiKey = apiKey,
            )
            .placeLimitBuyOrder(
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
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(
                exchangeName = exchangeName,
                apiKey = apiKey,
            )
            .placeLimitSellOrder(
                currencyPair = currencyPair,
                sellPrice = sellPrice,
                amount = amount,
            )
    }

    override fun getOpenOrders(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): List<Order> {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(
                exchangeName = exchangeName,
                apiKey = apiKey,
            )
            .getOpenOrders()
    }

    override fun getOpenOrders(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
    ): List<Order> {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(
                exchangeName = exchangeName,
                apiKey = apiKey,
            )
            .getOpenOrders(currencyPair = currencyPair)
    }
}
