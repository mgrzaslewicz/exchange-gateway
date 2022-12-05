package automate.profit.autocoin.order

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.order.ExchangeCancelOrderParams
import automate.profit.autocoin.exchange.order.ExchangeOrderType

data class CancelOrderRequestDto(
    val exchangeUserId: String,
    val exchangeName: String,
    val exchangeId: String,
    val orderId: String,
    val orderType: String,
    val currencyPair: CurrencyPairDto
) {
    fun toExchangeCancelOrderParams() = ExchangeCancelOrderParams(
        orderId = orderId,
        orderType = ExchangeOrderType.valueOf(orderType),
        exchangeName = exchangeName,
        currencyPair = CurrencyPair.of(currencyPair.baseCurrencyCode, currencyPair.counterCurrencyCode)
    )
}
