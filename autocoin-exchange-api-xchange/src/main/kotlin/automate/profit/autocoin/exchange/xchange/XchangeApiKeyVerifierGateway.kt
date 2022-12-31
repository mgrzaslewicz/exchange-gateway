package automate.profit.autocoin.exchange.xchange

import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.bitstamp
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.kucoin
import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.apikey.ApiKeyVerifierGateway
import java.util.function.Consumer
import java.util.function.Function


class XchangeApiKeyVerifierGateway(
    private val apiKeyVerifier: Consumer<ApiKey> = defaultApiKeyVerifier,
    private val exchangeSpecificApiKeyVerifier: Function<ExchangeName, Consumer<ApiKey>?> = defaultExchangeSpecificApiKeyVerifier,
) : ApiKeyVerifierGateway {
    companion object {
        val defaultApiKeyVerifier = Consumer<ApiKey> {
            if (it.publicKey.isEmpty()) throw IllegalArgumentException("Exchange api key is not provided")
            if (it.secretKey.isEmpty()) throw IllegalArgumentException("Exchange secret key is not provided")
            if (it.publicKey == it.secretKey) throw IllegalArgumentException("Secret key and api key cannot be the same")
        }
        val defaultExchangeSpecificApiKeyVerifiersMap = mapOf<ExchangeName, Consumer<ApiKey>>(
            bitstamp to Consumer { if (it.userName.isNullOrEmpty()) throw IllegalArgumentException("User name for $bitstamp is not provided") },
            kucoin to Consumer {
                val passphrase = it.exchangeSpecificKeyParameters?.get("passphrase")
                if (passphrase.isNullOrEmpty()) {
                    throw IllegalArgumentException("Passphrase for $kucoin is not provided")
                }
            },
        )
        val defaultExchangeSpecificApiKeyVerifier: Function<ExchangeName, Consumer<ApiKey>?> = Function {
            defaultExchangeSpecificApiKeyVerifiersMap[it]
        }
    }

    override fun verifyApiKey(
        exchangeName: ExchangeName,
        apiKey: ApiKey,
    ) {
        apiKeyVerifier.accept(apiKey)
        exchangeSpecificApiKeyVerifier.apply(exchangeName)?.accept(apiKey)
    }

}
