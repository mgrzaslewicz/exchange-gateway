package com.autocoin.exchangegateway.api.exchange.xchange

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import mu.KLogging
import java.util.function.Supplier
import org.knowm.xchange.ExchangeSpecification as XchangeExchangeSpecification

class XchangeSpecificationApiKeyAssigner(
    private val apiKeyVerifierGateway: com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeyVerifierGateway,
) {
    private companion object : KLogging()

    fun assignKeys(
        exchangeName: ExchangeName,
        exchangeSpecification: XchangeExchangeSpecification,
        apiKeySupplier: Supplier<com.autocoin.exchangegateway.spi.exchange.apikey.ApiKey>,
    ) {
        val apiKey = apiKeySupplier.get()
        apiKeyVerifierGateway.verifyApiKey(exchangeName = exchangeName, apiKey = apiKey)
        // do not change it to immutable, xchange needs a mutable map
        val exchangeSpecificParametersMap = if (apiKey.exchangeSpecificKeyParameters == null) mutableMapOf<String, String>() else HashMap(apiKey.exchangeSpecificKeyParameters)
        exchangeSpecification.apiKey = apiKey.publicKey.trim()
        exchangeSpecification.secretKey = apiKey.secretKey.trim()
        exchangeSpecification.userName = apiKey.userName
        // xchange lib needs mutable map as it sets default values for some implementations when these not provided
        exchangeSpecification.exchangeSpecificParameters = exchangeSpecificParametersMap as Map<String, Any>

        if (exchangeSpecification.apiKey != apiKey.publicKey) logger.warn("$exchangeName API public key contained whitespaces, trimmed")
        if (exchangeSpecification.secretKey != apiKey.secretKey) logger.warn("$exchangeName API secret key contained whitespaces, trimmed")
    }

}
