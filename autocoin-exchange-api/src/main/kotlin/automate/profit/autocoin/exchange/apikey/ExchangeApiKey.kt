package automate.profit.autocoin.exchange.apikey

/**
 * Use for exchange access which is out of autocoin user scope,
 * like getting public meta/market data which requires API key
 */
data class ExchangeApiKey(
    val publicKey: String,
    val secretKey: String,
    val userName: String? = null,
    val exchangeSpecificKeyParameters: Map<String, String>? = null
)
