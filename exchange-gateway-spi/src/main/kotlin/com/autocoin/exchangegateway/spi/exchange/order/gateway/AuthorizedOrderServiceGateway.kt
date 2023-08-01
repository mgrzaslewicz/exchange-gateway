package com.autocoin.exchangegateway.spi.exchange.order.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.CancelOrderParams
import com.autocoin.exchangegateway.spi.exchange.order.Order
import java.math.BigDecimal


interface AuthorizedOrderServiceGateway {

    fun cancelOrder(
        exchange: Exchange,
        cancelOrderParams: CancelOrderParams,
    ): Boolean

    fun placeMarketBuyOrderWithCounterCurrencyAmount(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order

    fun placeMarketBuyOrderWithBaseCurrencyAmount(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order

    fun placeMarketSellOrderWithCounterCurrencyAmount(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order

    fun placeMarketSellOrderWithBaseCurrencyAmount(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order

    fun placeLimitBuyOrder(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        buyPrice: BigDecimal,
        amount: BigDecimal,
    ): Order

    fun placeLimitSellOrder(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        sellPrice: BigDecimal,
        amount: BigDecimal,
    ): Order

    fun getOpenOrders(
        exchange: Exchange,
    ): List<Order>

    fun getOpenOrders(
        exchange: Exchange,
        currencyPair: CurrencyPair,
    ): List<Order>

}
