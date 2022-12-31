package automate.profit.autocoin.exchange.apikey

data class ExchangeKeyDto(
        val apiKey: String,
        val secretKey: String,
        val exchangeId: String,
        val exchangeUserId: String,
        val userName: String? = null,
        val exchangeSpecificKeyParameters: Map<String, String>?
)

interface ExchangeKeyService {
    /**
     * Returns exchange keys of user that has session/token
     */
    fun getExchangeKeys(): List<ExchangeKeyDto>

    /**
     * Returns exchange keys of exchangeUser that belongs to user that has session/token
     */
    fun getExchangeKeys(exchangeUserId: String): List<ExchangeKeyDto>

    fun getExchangeKey(exchangeUserId: String, exchangeId: String): ExchangeKeyDto?
}
