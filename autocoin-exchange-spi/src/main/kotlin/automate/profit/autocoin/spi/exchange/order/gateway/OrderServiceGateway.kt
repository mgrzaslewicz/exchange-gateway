package automate.profit.autocoin.spi.exchange.order.gateway

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.order.CancelOrderParams
import automate.profit.autocoin.spi.exchange.order.Order
import java.math.BigDecimal
import java.util.function.Supplier


interface OrderServiceGateway {

    fun cancelOrder(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>, cancelOrderParams: CancelOrderParams): Boolean

    fun placeMarketBuyOrderWithCounterCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order

    fun placeMarketBuyOrderWithBaseCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order

    fun placeMarketSellOrderWithCounterCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order

    fun placeMarketSellOrderWithBaseCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order

    fun placeLimitBuyOrder(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        buyPrice: BigDecimal,
        amount: BigDecimal,
    ): Order

    fun placeLimitSellOrder(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        sellPrice: BigDecimal,
        amount: BigDecimal,
    ): Order

    fun getOpenOrders(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
    ): List<Order>

    fun getOpenOrders(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
    ): List<Order>

}
