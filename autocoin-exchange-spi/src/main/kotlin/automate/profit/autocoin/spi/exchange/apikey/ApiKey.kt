package automate.profit.autocoin.spi.exchange.apikey

interface ApiKey {
    val publicKey: String
    val secretKey: String
    val userName: String?
    val exchangeSpecificKeyParameters: Map<String, String>?
}
