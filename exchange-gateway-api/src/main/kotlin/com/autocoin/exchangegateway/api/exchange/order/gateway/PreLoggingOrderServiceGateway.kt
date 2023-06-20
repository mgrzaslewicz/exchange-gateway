package com.autocoin.exchangegateway.api.exchange.order.gateway

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
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
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        cancelOrderParams: CancelOrderParams,
    ): Boolean {
        logger.info { "[${exchangeName.value}] Going to cancelOrder apiKey.id=${apiKey.id}, cancelOrderParams=$cancelOrderParams" }
        return decorated.cancelOrder(
            exchangeName = exchangeName,
            apiKey = apiKey,
            cancelOrderParams = cancelOrderParams,
        )
    }

    override fun placeLimitBuyOrder(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        buyPrice: BigDecimal,
        amount: BigDecimal,
    ): Order {
        logger.info { "[${exchangeName.value}] Going to placeLimitBuyOrder apiKey.id=${apiKey.id}, currencyPair=$currencyPair, buyPrice=${buyPrice.toPlainString()}, amount=${amount.toPlainString()}" }
        return decorated.placeLimitBuyOrder(
            exchangeName = exchangeName,
            apiKey = apiKey,
            currencyPair = currencyPair,
            buyPrice = buyPrice,
            amount = amount,
        )
    }

    override fun placeMarketBuyOrderWithCounterCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        logger.info { "[${exchangeName.value}] Going to placeLimitBuyOrder apiKey.id=${apiKey.id}, currencyPair=$currencyPair, currentPrice=${currentPrice.toPlainString()}" }
        return decorated.placeMarketBuyOrderWithCounterCurrencyAmount(
            exchangeName = exchangeName,
            apiKey = apiKey,
            currencyPair = currencyPair,
            counterCurrencyAmount = counterCurrencyAmount,
            currentPrice = currentPrice,
        )
    }

    override fun placeLimitSellOrder(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        sellPrice: BigDecimal,
        amount: BigDecimal,
    ): Order {
        logger.info { "[${exchangeName.value}] Going to placeLimitSellOrder apiKey.id=${apiKey.id}, currencyPair=$currencyPair, sellPrice=${sellPrice.toPlainString()}, amount=${amount.toPlainString()}" }
        return decorated.placeLimitSellOrder(
            exchangeName = exchangeName,
            apiKey = apiKey,
            currencyPair = currencyPair,
            sellPrice = sellPrice,
            amount = amount,
        )
    }

    override fun placeMarketSellOrderWithBaseCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        logger.info { "[${exchangeName.value}] Going to placeMarketSellOrderWithBaseCurrencyAmount apiKey.id=${apiKey.id}, currencyPair=$currencyPair, currentPrice=${currentPrice.toPlainString()}" }
        return decorated.placeMarketSellOrderWithBaseCurrencyAmount(
            exchangeName = exchangeName,
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
        logger.info { "[${exchangeName.value}] Going to placeMarketSellOrderWithCounterCurrencyAmount apiKey.id=${apiKey.id}, currencyPair=$currencyPair, currentPrice=${currentPrice.toPlainString()}" }
        return decorated.placeMarketSellOrderWithCounterCurrencyAmount(
            exchangeName = exchangeName,
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
        logger.info { "[$exchangeName] Going to placeMarketBuyOrderWithBaseCurrencyAmount apiKey.id=${apiKey.id}, currencyPair=$currencyPair, $baseCurrencyAmount=${baseCurrencyAmount.toPlainString()}, $currentPrice=${currentPrice.toPlainString()}" }
        return decorated.placeMarketBuyOrderWithBaseCurrencyAmount(
            exchangeName = exchangeName,
            apiKey = apiKey,
            currencyPair = currencyPair,
            baseCurrencyAmount = baseCurrencyAmount,
            currentPrice = currentPrice,
        )
    }

    override fun getOpenOrders(exchangeName: ExchangeName, apiKey: ApiKeySupplier<T>): List<Order> {
        logger.info { "[${exchangeName.value}] Going to getOpenOrders exchangeName=$exchangeName, apiKey.id=${apiKey.id}" }
        return decorated.getOpenOrders(
            exchangeName = exchangeName,
            apiKey = apiKey,
        )
    }

    override fun getOpenOrders(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
    ): List<Order> {
        logger.info { "[$exchangeName] Going to getOpenOrders apiKey.id=${apiKey.id}, currencyPair=$currencyPair" }
        return decorated.getOpenOrders(
            exchangeName = exchangeName,
            apiKey = apiKey,
            currencyPair = currencyPair,
        )
    }

}

fun <T> OrderServiceGateway<T>.preLogging() = PreLoggingOrderServiceGateway(this)
