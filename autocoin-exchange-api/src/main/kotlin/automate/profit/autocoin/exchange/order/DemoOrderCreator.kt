package automate.profit.autocoin.exchange.order

import automate.profit.autocoin.exchange.currency.CurrencyPair
import java.math.BigDecimal
import java.time.Clock

class DemoOrderCreator(private val clock: Clock) {

    fun placeLimitSellOrder(
        exchangeName: String,
        exchangeUserId: String,
        baseCurrencyCode: String,
        counterCurrencyCode: String,
        sellPrice: BigDecimal,
        amount: BigDecimal
    ): ExchangeOrder {
        val currentTimeMillis = clock.millis()
        return ExchangeOrder(
            exchangeName = exchangeName,
            status = ExchangeOrderStatus.NEW,
            currencyPair = CurrencyPair.of(base = baseCurrencyCode, counter = counterCurrencyCode),
            filledAmount = BigDecimal.ZERO,
            orderedAmount = amount,
            orderId = "$exchangeName-demo-$currentTimeMillis",
            price = sellPrice,
            receivedAtMillis = currentTimeMillis,
            exchangeTimestampMillis = null,
            type = ExchangeOrderType.ASK_SELL
        )
    }

    fun placeLimitBuyOrder(
        exchangeName: String,
        exchangeUserId: String,
        baseCurrencyCode: String,
        counterCurrencyCode: String,
        buyPrice: BigDecimal,
        amount: BigDecimal
    ): ExchangeOrder {
        val currentTimeMillis = clock.millis()
        return ExchangeOrder(
            exchangeName = exchangeName,
            status = ExchangeOrderStatus.NEW,
            currencyPair = CurrencyPair.of(base = baseCurrencyCode, counter = counterCurrencyCode),
            filledAmount = BigDecimal.ZERO,
            orderedAmount = amount,
            orderId = "$exchangeName-demo-${System.currentTimeMillis()}",
            price = buyPrice,
            receivedAtMillis = currentTimeMillis,
            exchangeTimestampMillis = null,
            type = ExchangeOrderType.BID_BUY
        )
    }

}
