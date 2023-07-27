package com.autocoin.exchangegateway.api.exchange.xchange

import com.autocoin.exchangegateway.api.exchange.xchange.SupportedXchangeExchange.bitstamp
import com.autocoin.exchangegateway.api.exchange.xchange.SupportedXchangeExchange.kucoin
import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKey
import java.util.function.Consumer
import java.util.function.Function


class XchangeApiKeyVerifierGateway(
    private val apiKeyVerifier: Consumer<ApiKey> = defaultApiKeyVerifier,
    private val exchangeSpecificApiKeyVerifier: Function<Exchange, Consumer<ApiKey>?> = defaultExchangeSpecificApiKeyVerifier,
) : com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeyVerifierGateway {
    companion object {
        val defaultApiKeyVerifier = Consumer<ApiKey> {
            if (it.publicKey.isEmpty()) throw IllegalArgumentException("Exchange api key is not provided")
            if (it.secretKey.isEmpty()) throw IllegalArgumentException("Exchange secret key is not provided")
            if (it.publicKey == it.secretKey) throw IllegalArgumentException("Secret key and api key cannot be the same")
        }
        val defaultExchangeSpecificApiKeyVerifiersMap =
            mapOf<XchangeExchange, Consumer<ApiKey>>(
                bitstamp to Consumer { if (it.userName.isNullOrEmpty()) throw IllegalArgumentException("User name for $bitstamp is not provided") },
                kucoin to Consumer {
                    val passphrase = it.exchangeSpecificKeyParameters?.get("passphrase")
                    if (passphrase.isNullOrEmpty()) {
                        throw IllegalArgumentException("Passphrase for $kucoin is not provided")
                    }
                },
            )
        val defaultExchangeSpecificApiKeyVerifier: Function<Exchange, Consumer<ApiKey>?> =
            Function {
                defaultExchangeSpecificApiKeyVerifiersMap[it]
            }
    }

    override fun verifyApiKey(
        exchange: Exchange,
        apiKey: ApiKey,
    ) {
        apiKeyVerifier.accept(apiKey)
        exchangeSpecificApiKeyVerifier.apply(exchange)?.accept(apiKey)
    }

}
