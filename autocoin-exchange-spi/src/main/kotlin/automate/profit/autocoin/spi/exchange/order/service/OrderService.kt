package automate.profit.autocoin.spi.exchange.order.service

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.order.CancelOrderParams
import automate.profit.autocoin.spi.exchange.order.Order
import java.math.BigDecimal
import java.util.function.Supplier

interface OrderService {
    val exchangeName: ExchangeName
    fun cancelOrder(apiKey: Supplier<ApiKey>, cancelOrderParams: CancelOrderParams): Boolean

    fun placeMarketBuyOrderWithCounterCurrencyAmount(
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order

    fun placeMarketBuyOrderWithBaseCurrencyAmount(
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order

    fun placeMarketSellOrderWithCounterCurrencyAmount(
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order

    fun placeMarketSellOrderWithBaseCurrencyAmount(
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order

    fun placeLimitBuyOrder(
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        buyPrice: BigDecimal,
        amount: BigDecimal,
    ): Order

    fun placeLimitSellOrder(
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        sellPrice: BigDecimal,
        amount: BigDecimal,
    ): Order

    fun getOpenOrders(apiKey: Supplier<ApiKey>): List<Order>

    fun getOpenOrders(apiKey: Supplier<ApiKey>, currencyPair: CurrencyPair): List<Order>
}

