package com.autocoin.exchangegateway.api.exchange.order

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.CancelOrderParams
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide
import com.autocoin.exchangegateway.spi.exchange.order.OrderStatus
import com.autocoin.exchangegateway.spi.exchange.order.gateway.OrderServiceGateway
import java.math.BigDecimal
import java.time.Clock
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair as SpiCurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.Order as SpiOrder

class DemoOrderServiceGateway<T>(private val clock: Clock) : OrderServiceGateway<T> {

    override fun cancelOrder(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        cancelOrderParams: CancelOrderParams,
    ): Boolean {
        return true
    }

    override fun getOpenOrders(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): List<SpiOrder> = emptyList()

    override fun getOpenOrders(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
    ): List<SpiOrder> = emptyList()

    override fun placeLimitSellOrder(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: SpiCurrencyPair,
        sellPrice: BigDecimal,
        amount: BigDecimal,
    ): SpiOrder {
        val currentTimeMillis = clock.millis()
        return Order(
            exchange = exchange,
            status = OrderStatus.NEW,
            currencyPair = currencyPair,
            filledAmount = BigDecimal.ZERO,
            orderedAmount = amount,
            exchangeOrderId = "$exchange-demo-$currentTimeMillis",
            price = sellPrice,
            receivedAtMillis = currentTimeMillis,
            exchangeTimestampMillis = null,
            side = OrderSide.ASK_SELL,
        )
    }

    override fun placeLimitBuyOrder(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: SpiCurrencyPair,
        buyPrice: BigDecimal,
        amount: BigDecimal,
    ): SpiOrder {
        val currentTimeMillis = clock.millis()
        return Order(
            exchange = exchange,
            status = OrderStatus.NEW,
            currencyPair = currencyPair,
            filledAmount = BigDecimal.ZERO,
            orderedAmount = amount,
            exchangeOrderId = "$exchange-demo-${System.currentTimeMillis()}",
            price = buyPrice,
            receivedAtMillis = currentTimeMillis,
            exchangeTimestampMillis = null,
            side = OrderSide.BID_BUY,
        )
    }

    override fun placeMarketBuyOrderWithCounterCurrencyAmount(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): SpiOrder {
        val currentTimeMillis = clock.millis()
        return Order(
            exchange = exchange,
            status = OrderStatus.NEW,
            currencyPair = currencyPair,
            filledAmount = BigDecimal.ZERO,
            orderedAmount = counterCurrencyAmount.div(currentPrice),
            exchangeOrderId = "$exchange-demo-market-buy-order-${System.currentTimeMillis()}",
            price = currentPrice,
            receivedAtMillis = currentTimeMillis,
            exchangeTimestampMillis = null,
            side = OrderSide.BID_BUY,
        )
    }

    override fun placeMarketBuyOrderWithBaseCurrencyAmount(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): SpiOrder {
        val currentTimeMillis = clock.millis()
        return Order(
            exchange = exchange,
            status = OrderStatus.NEW,
            currencyPair = currencyPair,
            filledAmount = BigDecimal.ZERO,
            orderedAmount = baseCurrencyAmount,
            exchangeOrderId = "$exchange-demo-market-buy-order-${System.currentTimeMillis()}",
            price = currentPrice,
            receivedAtMillis = currentTimeMillis,
            exchangeTimestampMillis = null,
            side = OrderSide.BID_BUY,
        )
    }

    override fun placeMarketSellOrderWithCounterCurrencyAmount(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): SpiOrder {
        val currentTimeMillis = clock.millis()
        return Order(
            exchange = exchange,
            status = OrderStatus.NEW,
            currencyPair = currencyPair,
            filledAmount = BigDecimal.ZERO,
            orderedAmount = counterCurrencyAmount.div(currentPrice),
            exchangeOrderId = "$exchange-demo-market-sell-order-${System.currentTimeMillis()}",
            price = currentPrice,
            receivedAtMillis = currentTimeMillis,
            exchangeTimestampMillis = null,
            side = OrderSide.ASK_SELL,
        )
    }

    override fun placeMarketSellOrderWithBaseCurrencyAmount(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): SpiOrder {
        val currentTimeMillis = clock.millis()
        return Order(
            exchange = exchange,
            status = OrderStatus.NEW,
            currencyPair = currencyPair,
            filledAmount = BigDecimal.ZERO,
            orderedAmount = baseCurrencyAmount,
            exchangeOrderId = "$exchange-demo-market-sell-order-${System.currentTimeMillis()}",
            price = currentPrice,
            receivedAtMillis = currentTimeMillis,
            exchangeTimestampMillis = null,
            side = OrderSide.ASK_SELL,
        )
    }

}
