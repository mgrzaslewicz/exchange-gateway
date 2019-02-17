package automate.profit.autocoin.exchange.apikey

data class Exchange(
        val id: String,
        val name: String,
        val enabled: Boolean
)

interface ExchangeService {
    fun getExchanges(): List<Exchange>
    fun getExchangeIdByName(exchangeName: String): String
    fun getExchangeNameById(exchangeId: String): String
}
