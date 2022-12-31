package automate.profit.autocoin.api.exchange

import java.util.function.Supplier
import automate.profit.autocoin.spi.exchange.apikey.ApiKey as SpiApiKey
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier as SpiApiKeySupplier

data class ApiKey(
    override val publicKey: String,
    override val secretKey: String,
    override val userName: String? = null,
    override val exchangeSpecificKeyParameters: Map<String, String>? = null,
) : SpiApiKey {
    init {
        check(publicKey.isNotBlank()) { "publicKey cannot be blank" }
        check(secretKey.isNotBlank()) { "secretKey cannot be blank" }
    }

    override fun toString() = "ApiKey(publicKey hashcode='${publicKey.hashCode()}', secretKey hashcode='${
        secretKey.hashCode()
    }', userName=${userName.hashCode()}"
}

data class ApiKeySupplier<T>(
    override val id: T,
    override val supplier: Supplier<SpiApiKey>?,
) : SpiApiKeySupplier<T>
