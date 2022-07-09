package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

interface ExchangeMetadataService {

    fun getAllExchangesMetadata(): List<ExchangeMetadata> {
        return SupportedExchange.values().mapNotNull {
            try {
                getMetadata(it.exchangeName)
            } catch (e: Exception) {
                logger.error(e) { "[${it.exchangeName}] Could not get metadata" }
                null
            }
        }
    }

    fun getMetadata(exchangeName: String): ExchangeMetadata
    fun getCurrencyPairMetadata(exchangeName: String, currencyPair: CurrencyPair): CurrencyPairMetadata? = getMetadata(exchangeName).currencyPairMetadata[currencyPair]
    fun getCurrencyMetadata(exchangeName: String, currency: String): CurrencyMetadata? = getMetadata(exchangeName).currencyMetadata[currency]
}
