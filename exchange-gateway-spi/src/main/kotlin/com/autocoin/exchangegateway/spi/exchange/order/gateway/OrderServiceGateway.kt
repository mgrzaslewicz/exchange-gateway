package com.autocoin.exchangegateway.spi.exchange.order.gateway

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.CancelOrderParams
import com.autocoin.exchangegateway.spi.exchange.order.Order
import java.math.BigDecimal


interface OrderServiceGateway<T> {

    fun cancelOrder(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        cancelOrderParams: CancelOrderParams,
    ): Boolean

    fun placeMarketBuyOrderWithCounterCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order

    fun placeMarketBuyOrderWithBaseCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order

    fun placeMarketSellOrderWithCounterCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order

    fun placeMarketSellOrderWithBaseCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order

    fun placeLimitBuyOrder(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        buyPrice: BigDecimal,
        amount: BigDecimal,
    ): Order

    fun placeLimitSellOrder(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        sellPrice: BigDecimal,
        amount: BigDecimal,
    ): Order

    fun getOpenOrders(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): List<Order>

    fun getOpenOrders(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
    ): List<Order>

}
