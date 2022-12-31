package automate.profit.autocoin.spi.exchange.order.service.authorized

import automate.profit.autocoin.spi.exchange.AuthorizedService
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.order.CancelOrderParams
import automate.profit.autocoin.spi.exchange.order.Order
import java.math.BigDecimal

interface AuthorizedOrderService<T>: AuthorizedService<T> {

    fun cancelOrder(cancelOrderParams: CancelOrderParams): Boolean

    fun placeMarketBuyOrderWithCounterCurrencyAmount(
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order

    fun placeMarketBuyOrderWithBaseCurrencyAmount(
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order

    fun placeMarketSellOrderWithCounterCurrencyAmount(
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order

    fun placeMarketSellOrderWithBaseCurrencyAmount(
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order

    /**
     * Buy currencyPair.base for currencyPair.base
     */
    fun placeLimitBuyOrder(
        currencyPair: CurrencyPair,
        buyPrice: BigDecimal,
        amount: BigDecimal,
    ): Order

    /**
     * Sell currencyPair.base currency and gain currencyPair.counter
     */
    fun placeLimitSellOrder(
        currencyPair: CurrencyPair,
        sellPrice: BigDecimal,
        amount: BigDecimal,
    ): Order

    fun getOpenOrders(): List<Order>
    fun getOpenOrders(currencyPair: CurrencyPair): List<Order>
}
