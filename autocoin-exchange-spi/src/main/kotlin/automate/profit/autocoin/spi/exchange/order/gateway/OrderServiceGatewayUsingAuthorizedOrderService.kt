package automate.profit.autocoin.spi.exchange.order.gateway

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.order.CancelOrderParams
import automate.profit.autocoin.spi.exchange.order.Order
import automate.profit.autocoin.spi.exchange.order.service.authorized.AuthorizedOrderServiceFactory
import java.math.BigDecimal
import java.util.function.Supplier

class OrderServiceGatewayUsingAuthorizedOrderService(
    private val authorizedOrderServiceFactory: AuthorizedOrderServiceFactory,
) : OrderServiceGateway {

    override fun cancelOrder(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>, cancelOrderParams: CancelOrderParams): Boolean {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(exchangeName = exchangeName, apiKey = apiKey)
            .cancelOrder(cancelOrderParams)
    }

    override fun placeMarketBuyOrderWithCounterCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(exchangeName = exchangeName, apiKey = apiKey)
            .placeMarketBuyOrderWithCounterCurrencyAmount(
                currencyPair = currencyPair,
                counterCurrencyAmount = counterCurrencyAmount,
                currentPrice = currentPrice,
            )
    }

    override fun placeMarketBuyOrderWithBaseCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(exchangeName = exchangeName, apiKey = apiKey)
            .placeMarketBuyOrderWithBaseCurrencyAmount(
                currencyPair = currencyPair,
                baseCurrencyAmount = baseCurrencyAmount,
                currentPrice = currentPrice,
            )
    }

    override fun placeMarketSellOrderWithCounterCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(exchangeName = exchangeName, apiKey = apiKey)
            .placeMarketSellOrderWithCounterCurrencyAmount(
                currencyPair = currencyPair,
                counterCurrencyAmount = counterCurrencyAmount,
                currentPrice = currentPrice,
            )
    }

    override fun placeMarketSellOrderWithBaseCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): Order {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(exchangeName = exchangeName, apiKey = apiKey)
            .placeMarketSellOrderWithBaseCurrencyAmount(
                currencyPair = currencyPair,
                baseCurrencyAmount = baseCurrencyAmount,
                currentPrice = currentPrice,
            )
    }

    override fun placeLimitBuyOrder(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>, currencyPair: CurrencyPair, buyPrice: BigDecimal, amount: BigDecimal): Order {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(exchangeName = exchangeName, apiKey = apiKey)
            .placeLimitBuyOrder(currencyPair = currencyPair, buyPrice = buyPrice, amount = amount)
    }

    override fun placeLimitSellOrder(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>, currencyPair: CurrencyPair, sellPrice: BigDecimal, amount: BigDecimal): Order {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(exchangeName = exchangeName, apiKey = apiKey)
            .placeLimitSellOrder(currencyPair = currencyPair, sellPrice = sellPrice, amount = amount)
    }

    override fun getOpenOrders(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>): List<Order> {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(exchangeName = exchangeName, apiKey = apiKey)
            .getOpenOrders()
    }

    override fun getOpenOrders(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>, currencyPair: CurrencyPair): List<Order> {
        return authorizedOrderServiceFactory
            .createAuthorizedOrderService(exchangeName = exchangeName, apiKey = apiKey)
            .getOpenOrders(currencyPair = currencyPair)
    }
}
