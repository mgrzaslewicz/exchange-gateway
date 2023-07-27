package com.autocoin.exchangegateway.api.exchange.order.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.CancelOrderParams
import com.autocoin.exchangegateway.spi.exchange.order.Order
import com.autocoin.exchangegateway.spi.exchange.order.gateway.OrderServiceGateway
import mu.KLogging
import java.math.BigDecimal
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class AddingDelayOrderServiceGateway<T>(
    private val decorated: OrderServiceGateway<T>,
    private val delay: Duration = Duration.ofSeconds(1),
) : OrderServiceGateway<T> by decorated {
    companion object : KLogging()

    override fun cancelOrder(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        cancelOrderParams: CancelOrderParams,
    ): Boolean {
        nonThreadBlockingSleep(delay)
        return decorated.cancelOrder(
            exchange = exchange,
            apiKey = apiKey,
            cancelOrderParams = cancelOrderParams,
        )
    }

    private fun nonThreadBlockingSleep(delay: Duration) {
        val schedule = Executors.newSingleThreadScheduledExecutor().schedule(
            {},
            delay.toMillis(),
            TimeUnit.MILLISECONDS,
        )
        schedule.get()
    }

    override fun placeLimitBuyOrder(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
        buyPrice: BigDecimal,
        amount: BigDecimal,
    ): Order {
        nonThreadBlockingSleep(delay)
        return decorated.placeLimitBuyOrder(
            exchange = exchange,
            apiKey = apiKey,
            currencyPair = currencyPair,
            buyPrice = buyPrice,
            amount = amount,
        )
    }

}

fun <T> OrderServiceGateway<T>.addingDelay(delay: Duration = Duration.ofSeconds(1)) =
    AddingDelayOrderServiceGateway(this, delay = delay)
