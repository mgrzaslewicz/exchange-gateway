package automate.profit.autocoin.exchange.wallet

data class ExchangeBalanceDto(
    val exchangeName: String,
    val currencyBalances: List<ExchangeCurrencyBalanceDto>,
    val errorMessage: String?
)
