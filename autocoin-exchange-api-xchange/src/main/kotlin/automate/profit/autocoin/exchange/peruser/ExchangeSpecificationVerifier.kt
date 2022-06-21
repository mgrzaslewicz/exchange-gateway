package automate.profit.autocoin.exchange.peruser

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.SupportedExchange.BITSTAMP
import automate.profit.autocoin.exchange.SupportedExchange.KUCOIN
import org.knowm.xchange.ExchangeSpecification

class ExchangeSpecificationVerifier {

    fun verifyKeys(supportedExchange: SupportedExchange, exchangeSpecification: ExchangeSpecification) {
        if (exchangeSpecification.apiKey.isNullOrEmpty()) throw IllegalArgumentException("Exchange api key is not provided")
        if (exchangeSpecification.secretKey.isNullOrEmpty()) throw IllegalArgumentException("Exchange secret key is not provided")
        if (exchangeSpecification.apiKey == exchangeSpecification.secretKey) throw IllegalArgumentException("Secret key and api key cannot be the same")

        when (supportedExchange) {
            BITSTAMP -> if (exchangeSpecification.userName.isNullOrEmpty()) throw IllegalArgumentException("User name for bitstamp is not provided")
            KUCOIN -> {
                val passphrase = exchangeSpecification.getExchangeSpecificParametersItem("passphrase") as String?
                if (passphrase.isNullOrEmpty()) {
                    throw IllegalArgumentException("Passphrase for kucoin is not provided")
                }

            }
            else -> {}
        }
    }

}
