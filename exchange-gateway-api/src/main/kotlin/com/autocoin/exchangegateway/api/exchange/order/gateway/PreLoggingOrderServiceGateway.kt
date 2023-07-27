package com.autocoin.exchangegateway.api.exchange.order.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.CancelOrderParams
import com.autocoin.exchangegateway.spi.exchange.order.Order
import com.autocoin.exchangegateway.spi.exchange.order.gateway.OrderServiceGateway
import mu.KLogging
import java.math.BigDecimal

class PreLoggingOrderServiceGateway<T>(
    private val decorated: OrderServiceGateway<T>,
) : OrderServiceGateway<T> {
    companion object : KLogging()

    override fun cancelOrder(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        cancelOrderParams: CancelOrderParams,
    ): Boolean {
        logger.info { "[${exchange.exchangeName}] Going to cancelOrder apiKey.id=${apiKey.id}, cancelOrderParams=$cancelOrderParams" }
        return decorated.cancelOrder(
            exchange = exchange,
            apiKey = apiKey,
            cancelOrderParams = cancelOrderParams,
        )
    }

    override fun placeLimitBuyOrder(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        buyPrice: BigDecimal,
        amount: BigDecimal,
    ): Order {
        logger.info { "[${exchange.exchangeName}] Going to placeLimitBuyOrder apiKey.id=${apiKey.id}, currencyPair=$currencyPair, buyPrice=${buyPrice.toPlainString()}, amount=${amount.toPlainString()}" }
        return decorated.placeLimitBuyOrder(
            exchange = exchange,
            apiKey = apiKey,
            currencyPair = currencyPair,
            buyPrice = buyPrice,
            amount = amount,
        )
    }

    override fun placeMarketBuyOrderWithCounterCurrencyAmount(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        logger.info { "[${exchange.exchangeName}] Going to placeLimitBuyOrder apiKey.id=${apiKey.id}, currencyPair=$currencyPair, currentPrice=${currentPrice.toPlainString()}" }
        return decorated.placeMarketBuyOrderWithCounterCurrencyAmount(
            exchange = exchange,
            apiKey = apiKey,
            currencyPair = currencyPair,
            counterCurrencyAmount = counterCurrencyAmount,
            currentPrice = currentPrice,
        )
    }

    override fun placeLimitSellOrder(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        sellPrice: BigDecimal,
        amount: BigDecimal,
    ): Order {
        logger.info { "[${exchange.exchangeName}] Going to placeLimitSellOrder apiKey.id=${apiKey.id}, currencyPair=$currencyPair, sellPrice=${sellPrice.toPlainString()}, amount=${amount.toPlainString()}" }
        return decorated.placeLimitSellOrder(
            exchange = exchange,
            apiKey = apiKey,
            currencyPair = currencyPair,
            sellPrice = sellPrice,
            amount = amount,
        )
    }

    override fun placeMarketSellOrderWithBaseCurrencyAmount(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        logger.info { "[${exchange.exchangeName}] Going to placeMarketSellOrderWithBaseCurrencyAmount apiKey.id=${apiKey.id}, currencyPair=$currencyPair, currentPrice=${currentPrice.toPlainString()}" }
        return decorated.placeMarketSellOrderWithBaseCurrencyAmount(
            exchange = exchange,
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
        logger.info { "[${exchange.exchangeName}] Going to placeMarketSellOrderWithCounterCurrencyAmount apiKey.id=${apiKey.id}, currencyPair=$currencyPair, currentPrice=${currentPrice.toPlainString()}" }
        return decorated.placeMarketSellOrderWithCounterCurrencyAmount(
            exchange = exchange,
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
        logger.info { "[$exchange] Going to placeMarketBuyOrderWithBaseCurrencyAmount apiKey.id=${apiKey.id}, currencyPair=$currencyPair, $baseCurrencyAmount=${baseCurrencyAmount.toPlainString()}, $currentPrice=${currentPrice.toPlainString()}" }
        return decorated.placeMarketBuyOrderWithBaseCurrencyAmount(
            exchange = exchange,
            apiKey = apiKey,
            currencyPair = currencyPair,
            baseCurrencyAmount = baseCurrencyAmount,
            currentPrice = currentPrice,
        )
    }

    override fun getOpenOrders(exchange: Exchange, apiKey: ApiKeySupplier<T>): List<Order> {
        logger.info { "[${exchange.exchangeName}] Going to getOpenOrders exchangeName=$exchange, apiKey.id=${apiKey.id}" }
        return decorated.getOpenOrders(
            exchange = exchange,
            apiKey = apiKey,
        )
    }

    override fun getOpenOrders(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
    ): List<Order> {
        logger.info { "[$exchange] Going to getOpenOrders apiKey.id=${apiKey.id}, currencyPair=$currencyPair" }
        return decorated.getOpenOrders(
            exchange = exchange,
            apiKey = apiKey,
            currencyPair = currencyPair,
        )
    }

}

fun <T> OrderServiceGateway<T>.preLogging() = PreLoggingOrderServiceGateway(this)
