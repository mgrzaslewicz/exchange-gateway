package automate.profit.autocoin.api.exchange.order

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.order.CancelOrderParams
import automate.profit.autocoin.spi.exchange.order.OrderSide
import automate.profit.autocoin.spi.exchange.order.OrderStatus
import automate.profit.autocoin.spi.exchange.order.gateway.OrderServiceGateway
import java.math.BigDecimal
import java.time.Clock
import java.util.function.Supplier
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair as SpiCurrencyPair
import automate.profit.autocoin.spi.exchange.order.Order as SpiOrder

class DemoOrderServiceGateway(private val clock: Clock) : OrderServiceGateway {

    override fun cancelOrder(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>, cancelOrderParams: CancelOrderParams): Boolean {
        return true
    }

    override fun getOpenOrders(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>): List<SpiOrder> = emptyList()
    override fun getOpenOrders(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>, currencyPair: CurrencyPair): List<SpiOrder> = emptyList()

    override fun placeLimitSellOrder(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: SpiCurrencyPair,
        sellPrice: BigDecimal,
        amount: BigDecimal,
    ): SpiOrder {
        val currentTimeMillis = clock.millis()
        return Order(
            exchangeName = exchangeName,
            status = OrderStatus.NEW,
            currencyPair = currencyPair,
            filledAmount = BigDecimal.ZERO,
            orderedAmount = amount,
            exchangeOrderId = "$exchangeName-demo-$currentTimeMillis",
            price = sellPrice,
            receivedAtMillis = currentTimeMillis,
            exchangeTimestampMillis = null,
            side = OrderSide.ASK_SELL,
        )
    }

    override fun placeLimitBuyOrder(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: SpiCurrencyPair,
        buyPrice: BigDecimal,
        amount: BigDecimal,
    ): SpiOrder {
        val currentTimeMillis = clock.millis()
        return Order(
            exchangeName = exchangeName,
            status = OrderStatus.NEW,
            currencyPair = currencyPair,
            filledAmount = BigDecimal.ZERO,
            orderedAmount = amount,
            exchangeOrderId = "$exchangeName-demo-${System.currentTimeMillis()}",
            price = buyPrice,
            receivedAtMillis = currentTimeMillis,
            exchangeTimestampMillis = null,
            side = OrderSide.BID_BUY,
        )
    }

    override fun placeMarketBuyOrderWithCounterCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): SpiOrder {
        val currentTimeMillis = clock.millis()
        return Order(
            exchangeName = exchangeName,
            status = OrderStatus.NEW,
            currencyPair = currencyPair,
            filledAmount = BigDecimal.ZERO,
            orderedAmount = counterCurrencyAmount.div(currentPrice),
            exchangeOrderId = "$exchangeName-demo-market-buy-order-${System.currentTimeMillis()}",
            price = currentPrice,
            receivedAtMillis = currentTimeMillis,
            exchangeTimestampMillis = null,
            side = OrderSide.BID_BUY,
        )
    }

    override fun placeMarketBuyOrderWithBaseCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): SpiOrder {
        val currentTimeMillis = clock.millis()
        return Order(
            exchangeName = exchangeName,
            status = OrderStatus.NEW,
            currencyPair = currencyPair,
            filledAmount = BigDecimal.ZERO,
            orderedAmount = baseCurrencyAmount,
            exchangeOrderId = "$exchangeName-demo-market-buy-order-${System.currentTimeMillis()}",
            price = currentPrice,
            receivedAtMillis = currentTimeMillis,
            exchangeTimestampMillis = null,
            side = OrderSide.BID_BUY,
        )
    }

    override fun placeMarketSellOrderWithCounterCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        counterCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): SpiOrder {
        val currentTimeMillis = clock.millis()
        return Order(
            exchangeName = exchangeName,
            status = OrderStatus.NEW,
            currencyPair = currencyPair,
            filledAmount = BigDecimal.ZERO,
            orderedAmount = counterCurrencyAmount.div(currentPrice),
            exchangeOrderId = "$exchangeName-demo-market-sell-order-${System.currentTimeMillis()}",
            price = currentPrice,
            receivedAtMillis = currentTimeMillis,
            exchangeTimestampMillis = null,
            side = OrderSide.ASK_SELL,
        )
    }

    override fun placeMarketSellOrderWithBaseCurrencyAmount(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyPair: CurrencyPair,
        baseCurrencyAmount: BigDecimal,
        currentPrice: BigDecimal,
    ): SpiOrder {
        val currentTimeMillis = clock.millis()
        return Order(
            exchangeName = exchangeName,
            status = OrderStatus.NEW,
            currencyPair = currencyPair,
            filledAmount = BigDecimal.ZERO,
            orderedAmount = baseCurrencyAmount,
            exchangeOrderId = "$exchangeName-demo-market-sell-order-${System.currentTimeMillis()}",
            price = currentPrice,
            receivedAtMillis = currentTimeMillis,
            exchangeTimestampMillis = null,
            side = OrderSide.ASK_SELL,
        )
    }


}
