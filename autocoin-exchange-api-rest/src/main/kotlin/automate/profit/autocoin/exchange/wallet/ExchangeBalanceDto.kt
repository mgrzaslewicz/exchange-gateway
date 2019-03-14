package automate.profit.autocoin.exchange.wallet

data class ExchangeBalanceDto(
        val exchangeName: String,
        val currencyBalances: List<CurrencyBalanceDto>,
        val errorMessage: String?
)
