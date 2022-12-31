package com.autocoin.exchangegateway.api.exchange.xchange

import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.bitstamp
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.kucoin
import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import java.util.function.Consumer
import java.util.function.Function


class XchangeApiKeyVerifierGateway(
    private val apiKeyVerifier: Consumer<com.autocoin.exchangegateway.spi.exchange.apikey.ApiKey> = defaultApiKeyVerifier,
    private val exchangeSpecificApiKeyVerifier: Function<ExchangeName, Consumer<com.autocoin.exchangegateway.spi.exchange.apikey.ApiKey>?> = defaultExchangeSpecificApiKeyVerifier,
) : com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeyVerifierGateway {
    companion object {
        val defaultApiKeyVerifier = Consumer<com.autocoin.exchangegateway.spi.exchange.apikey.ApiKey> {
            if (it.publicKey.isEmpty()) throw IllegalArgumentException("Exchange api key is not provided")
            if (it.secretKey.isEmpty()) throw IllegalArgumentException("Exchange secret key is not provided")
            if (it.publicKey == it.secretKey) throw IllegalArgumentException("Secret key and api key cannot be the same")
        }
        val defaultExchangeSpecificApiKeyVerifiersMap =
            mapOf<ExchangeName, Consumer<com.autocoin.exchangegateway.spi.exchange.apikey.ApiKey>>(
                bitstamp to Consumer { if (it.userName.isNullOrEmpty()) throw IllegalArgumentException("User name for $bitstamp is not provided") },
                kucoin to Consumer {
                    val passphrase = it.exchangeSpecificKeyParameters?.get("passphrase")
                    if (passphrase.isNullOrEmpty()) {
                        throw IllegalArgumentException("Passphrase for $kucoin is not provided")
                    }
                },
            )
        val defaultExchangeSpecificApiKeyVerifier: Function<ExchangeName, Consumer<com.autocoin.exchangegateway.spi.exchange.apikey.ApiKey>?> =
            Function {
                defaultExchangeSpecificApiKeyVerifiersMap[it]
            }
    }

    override fun verifyApiKey(
        exchangeName: ExchangeName,
        apiKey: com.autocoin.exchangegateway.spi.exchange.apikey.ApiKey,
    ) {
        apiKeyVerifier.accept(apiKey)
        exchangeSpecificApiKeyVerifier.apply(exchangeName)?.accept(apiKey)
    }

}
