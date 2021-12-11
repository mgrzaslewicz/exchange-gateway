package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.apikey.ExchangeApiKey
import automate.profit.autocoin.exchange.apikey.ServiceApiKeysProvider
import automate.profit.autocoin.exchange.currency.CurrencyPair
import mu.KLogging
import java.io.File

/**
 * Will fetch and save metadata if there is none for given exchange yet
 */
class ExchangeMetadataProvider(
        exchangeMetadataFetchers: List<ExchangeMetadataFetcher>,
        private val exchangeMetadataRepository: FileExchangeMetadataRepository,
        private val serviceApiKeysProvider: ServiceApiKeysProvider
) : ExchangeMetadataService {

    init {
        check(exchangeMetadataFetchers
                .map { it.supportedExchange }
                .toSet().size >= exchangeMetadataFetchers.size
        ) { "Provided fetchers are invalid, there are duplicated ones" }
    }

    companion object : KLogging()

    override fun getMetadata(exchangeName: String): ExchangeMetadata {
        return getAndSaveExchangeMetadata(SupportedExchange.fromExchangeName(exchangeName))
    }

    override fun getMetadata(exchangeName: String, currencyPair: CurrencyPair): CurrencyPairMetadata {
        return getAndSaveExchangeMetadata(SupportedExchange.fromExchangeName(exchangeName)).currencyPairMetadata.getValue(currencyPair)
    }

    private val fetchersMap = exchangeMetadataFetchers.associateBy { it.supportedExchange }

    fun getAndSaveExchangeMetadata(supportedExchange: SupportedExchange): ExchangeMetadata {
        logger.info { "[$supportedExchange] Getting  metadata" }
        val exchangeMetadata = exchangeMetadataRepository.getLatestExchangeMetadata(supportedExchange)
        return if (exchangeMetadata != null) {
            exchangeMetadata
        } else {
            logger.info { "[$supportedExchange] Fetching exchange metadata" }
            val apiKey =  serviceApiKeysProvider.getApiKeys(supportedExchange)
            val (xchangeMetadataJson, freshExchangeMetadata) = fetchersMap.getValue(supportedExchange).fetchExchangeMetadata(apiKey)
            exchangeMetadataRepository.saveExchangeMetadata(supportedExchange, freshExchangeMetadata, xchangeMetadataJson)
            return freshExchangeMetadata
        }
    }

    fun getAndSaveXchangeMetadataFile(supportedExchange: SupportedExchange): File {
        logger.info { "[$supportedExchange] Getting xchange metadata file" }
        val exchangeMetadataFile = exchangeMetadataRepository.getLatestXchangeMetadataFile(supportedExchange)
        return if (exchangeMetadataFile != null) {
            exchangeMetadataFile
        } else {
            logger.info { "[$supportedExchange] Fetching xchange metadata file" }
            val (xchangeMetadataJson, freshExchangeMetadata) = fetchersMap.getValue(supportedExchange).fetchExchangeMetadata()
            exchangeMetadataRepository.saveExchangeMetadata(supportedExchange, freshExchangeMetadata, xchangeMetadataJson)
            return exchangeMetadataRepository.getLatestXchangeMetadataFile(supportedExchange)
                    ?: throw IllegalStateException("Something went wrong. $supportedExchange exchange metadata was just fetched, but it's not accessible")
        }
    }

}
