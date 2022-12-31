package automate.profit.autocoin.exchange

import automate.profit.autocoin.exchange.apikey.ExchangeApiKey
import automate.profit.autocoin.exchange.peruser.ExchangeSpecificationVerifier
import mu.KLogging
import org.knowm.xchange.ExchangeSpecification as XchangeExchangeSpecification

class XchangeSpecificationApiKeyAssigner(
    private val exchangeSpecificationVerifier: ExchangeSpecificationVerifier
) {
    private companion object : KLogging()

    fun assignKeys(supportedExchange: SupportedExchange, exchangeSpecification: XchangeExchangeSpecification, apiKey: ExchangeApiKey?) {
        if (apiKey != null) {
            assignKeys(supportedExchange, exchangeSpecification, apiKey.publicKey, apiKey.secretKey, apiKey.userName, apiKey.exchangeSpecificKeyParameters)
        }
    }

    fun assignKeys(
        supportedExchange: SupportedExchange,
        exchangeSpecification: XchangeExchangeSpecification,
        publicKey: String,
        secretKey: String,
        userName: String?,
        exchangeSpecificKeyParameters: Map<String, String>?
    ) {
        // do not change it to immutable, xchange needs a mutable map
        val exchangeSpecificParametersMap = if (exchangeSpecificKeyParameters == null) mutableMapOf<String, String>() else HashMap(exchangeSpecificKeyParameters)
        exchangeSpecification.apiKey = publicKey.trim()
        exchangeSpecification.secretKey = secretKey.trim()
        exchangeSpecification.userName = userName
        // xchange lib needs mutable map as it sets default values for some implementations when these not provided
        exchangeSpecification.exchangeSpecificParameters = exchangeSpecificParametersMap as Map<String, Any>

        if (exchangeSpecification.apiKey != publicKey) logger.warn("$supportedExchange API public key contained whitespaces, trimmed")
        if (exchangeSpecification.secretKey != secretKey) logger.warn("$supportedExchange API secret key contained whitespaces, trimmed")

        exchangeSpecificationVerifier.verifyKeys(supportedExchange, exchangeSpecification)
    }
}