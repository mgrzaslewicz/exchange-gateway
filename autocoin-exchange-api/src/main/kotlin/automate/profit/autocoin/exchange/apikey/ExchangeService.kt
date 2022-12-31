package automate.profit.autocoin.exchange.apikey

data class ExchangeDto(
    val id: String,
    val name: String,
    val enabled: Boolean
)

interface ExchangeService {
    fun getExchanges(): List<ExchangeDto>
    fun getExchangeIdByName(exchangeName: String): String
    fun getExchangeNameById(exchangeId: String): String
}
