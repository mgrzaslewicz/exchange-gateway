package automate.profit.autocoin.order

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.order.ExchangeCancelOrderParams
import automate.profit.autocoin.exchange.order.ExchangeOrderType

data class CancelOrderRequestDto(
        val exchangeUserId: String,
        val exchangeId: String,
        val orderId: String,
        val orderType: String,
        val currencyPair: CurrencyPairDto
) {
    fun toExchangeCancelOrderParams() = ExchangeCancelOrderParams(
            orderId = orderId,
            orderType = ExchangeOrderType.valueOf(orderType),
            currencyPair = CurrencyPair.invoke(currencyPair.baseCurrencyCode, currencyPair.counterCurrencyCode)
    )
}
