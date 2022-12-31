package automate.profit.autocoin.spi.exchange.apikey

import java.util.function.Supplier

interface ApiKey {
    val publicKey: String
    val secretKey: String
    val userName: String?
    val exchangeSpecificKeyParameters: Map<String, String>?
}

interface ApiKeySupplier<T> {
    val id: T
    val supplier: Supplier<ApiKey>?
}
