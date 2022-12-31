package automate.profit.autocoin.exchange.wallet

import automate.profit.autocoin.exchange.currency.ExchangeCurrencyBalance
import automate.profit.autocoin.exchange.order.ExchangeOrderType

data class CreateOrderDto(
    val exchangeName: String,
    val exchangeUserId: String,
    val orderType: ExchangeOrderType,
    val baseCurrencyCode: String,
    val counterCurrencyCode: String,
    val price: Double,
    val amount: Double
)

data class ExchangeCurrencyBalancesDto(
    val exchangeUserId: String,
    val exchangeUserName: String,
    val exchangeBalances: List<ExchangeBalanceDto>
)

fun ExchangeCurrencyBalance.toDto() = ExchangeCurrencyBalanceDto(
    currencyCode = this.currencyCode,
    amountAvailable = this.amountAvailable.toPlainString(),
    totalAmount = this.totalAmount.toPlainString(),
    amountInOrders = this.amountInOrders.toPlainString(),
)
