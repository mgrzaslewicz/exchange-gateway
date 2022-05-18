package automate.profit.autocoin.exchange.wallet

import automate.profit.autocoin.exchange.currency.CurrencyBalance
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

data class CurrencyBalancesDto(
    val exchangeUserId: String,
    val exchangeBalances: List<ExchangeBalanceDto>
)

fun CurrencyBalance.toDto() = CurrencyBalanceDto(
    currencyCode = this.currencyCode,
    available = this.available.toDouble(),
    total = this.total.toDouble(),
    frozen = this.frozen.toDouble()
)
