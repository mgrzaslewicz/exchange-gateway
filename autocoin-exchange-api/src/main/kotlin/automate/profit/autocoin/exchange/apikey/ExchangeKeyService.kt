package automate.profit.autocoin.exchange.apikey

data class ExchangeKey(
        val apiKey: String,
        val secretKey: String,
        val exchangeId: String,
        val exchangeUserId: String,
        val userName: String? = null
)

interface ExchangeKeyService {
 /**
  * Returns exchange keys of user that has session/token
  */
 fun getExchangeKeys(): List<ExchangeKey>

 /**
  * Returns exchange keys of exchangeUser that belongs to user that has session/token
  */
 fun getExchangeKeys(exchangeUserId: String): List<ExchangeKey>

 fun getExchangeKey(exchangeUserId: String, exchangeId: String): ExchangeKey?
}
