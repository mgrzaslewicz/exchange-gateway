package automate.profit.autocoin.spi.exchange.order.service

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.order.CancelOrderParams
import automate.profit.autocoin.spi.exchange.order.Order
import automate.profit.autocoin.spi.exchange.order.service.authorized.AuthorizedOrderServiceFactory
import java.math.BigDecimal
import java.util.function.Supplier

class OrderServiceUsingAuthorizedOrderService(
    override val exchangeName: ExchangeName,
    private val authorizedOrderServiceFactory: AuthorizedOrderServiceFactory,
) : OrderService {
    override fun cancelOrder(apiKey: Supplier<ApiKey>, cancelOrderParams: CancelOrderParams): Boolean {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(apiKey = apiKey, exchangeName = exchangeName)
            .cancelOrder(cancelOrderParams)
    }

    override fun placeMarketBuyOrderWithCounterCurrencyAmount(
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(apiKey = apiKey, exchangeName = exchangeName)
            .placeMarketBuyOrderWithCounterCurrencyAmount(
                currencyPair = currencyPair,
                counterCurrencyAmount = counterCurrencyAmount,
                currentPrice = currentPrice,
            )
    }

    override fun placeMarketBuyOrderWithBaseCurrencyAmount(apiKey: Supplier<ApiKey>, currencyPair: CurrencyPair, baseCurrencyAmount: BigDecimal, currentPrice: BigDecimal): Order {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(apiKey = apiKey, exchangeName = exchangeName)
            .placeMarketBuyOrderWithBaseCurrencyAmount(
                currencyPair = currencyPair,
                baseCurrencyAmount = baseCurrencyAmount,
                currentPrice = currentPrice,
            )
    }

    override fun placeMarketSellOrderWithCounterCurrencyAmount(
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(apiKey = apiKey, exchangeName = exchangeName)
            .placeMarketSellOrderWithCounterCurrencyAmount(
                currencyPair = currencyPair,
                counterCurrencyAmount = counterCurrencyAmount,
                currentPrice = currentPrice,
            )
    }

    override fun placeMarketSellOrderWithBaseCurrencyAmount(apiKey: Supplier<ApiKey>, currencyPair: CurrencyPair, baseCurrencyAmount: BigDecimal, currentPrice: BigDecimal): Order {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(apiKey = apiKey, exchangeName = exchangeName)
            .placeMarketSellOrderWithBaseCurrencyAmount(
                currencyPair = currencyPair,
                baseCurrencyAmount = baseCurrencyAmount,
                currentPrice = currentPrice,
            )
    }

    override fun placeLimitBuyOrder(apiKey: Supplier<ApiKey>, currencyPair: CurrencyPair, buyPrice: BigDecimal, amount: BigDecimal): Order {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(apiKey = apiKey, exchangeName = exchangeName)
            .placeLimitBuyOrder(
                currencyPair = currencyPair,
                buyPrice = buyPrice,
                amount = amount,
            )
    }

    override fun placeLimitSellOrder(apiKey: Supplier<ApiKey>, currencyPair: CurrencyPair, sellPrice: BigDecimal, amount: BigDecimal): Order {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(apiKey = apiKey, exchangeName = exchangeName)
            .placeLimitSellOrder(
                currencyPair = currencyPair,
                sellPrice = sellPrice,
                amount = amount,
            )
    }

    override fun getOpenOrders(apiKey: Supplier<ApiKey>): List<Order> {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(apiKey = apiKey, exchangeName = exchangeName)
            .getOpenOrders()
    }

    override fun getOpenOrders(apiKey: Supplier<ApiKey>, currencyPair: CurrencyPair): List<Order> {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(
                apiKey = apiKey,
                exchangeName = exchangeName,
            )
            .getOpenOrders(currencyPair = currencyPair)
    }
}
