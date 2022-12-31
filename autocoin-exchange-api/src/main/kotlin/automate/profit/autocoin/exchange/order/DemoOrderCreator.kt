package automate.profit.autocoin.exchange.order

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.time.TimeMillisProvider
import java.math.BigDecimal
import java.time.Instant

class DemoOrderCreator(private val timeMillisProvider: TimeMillisProvider) {

    fun placeLimitSellOrder(exchangeName: String, exchangeUserId: String, baseCurrencyCode: String, counterCurrencyCode: String, sellPrice: BigDecimal, amount: BigDecimal): ExchangeOrder {
        val currentTimeMillis = timeMillisProvider.now()
        return ExchangeOrder(
                exchangeName = exchangeName,
                status = ExchangeOrderStatus.NEW,
                currencyPair = CurrencyPair(base = baseCurrencyCode, counter = counterCurrencyCode),
                filledAmount = BigDecimal.ZERO,
                orderedAmount = amount,
                orderId = "$exchangeName-demo-$currentTimeMillis",
                price = sellPrice,
                timestamp = Instant.ofEpochMilli(currentTimeMillis),
                type = ExchangeOrderType.ASK_SELL
        )
    }

    fun placeLimitBuyOrder(exchangeName: String, exchangeUserId: String, baseCurrencyCode: String, counterCurrencyCode: String, buyPrice: BigDecimal, amount: BigDecimal): ExchangeOrder {
        val currentTimeMillis = timeMillisProvider.now()
        return ExchangeOrder(
                exchangeName = exchangeName,
                status = ExchangeOrderStatus.NEW,
                currencyPair = CurrencyPair(base = baseCurrencyCode, counter = counterCurrencyCode),
                filledAmount = BigDecimal.ZERO,
                orderedAmount = amount,
                orderId = "$exchangeName-demo-${System.currentTimeMillis()}",
                price = buyPrice,
                timestamp = Instant.ofEpochMilli(currentTimeMillis),
                type = ExchangeOrderType.BID_BUY
        )
    }

}