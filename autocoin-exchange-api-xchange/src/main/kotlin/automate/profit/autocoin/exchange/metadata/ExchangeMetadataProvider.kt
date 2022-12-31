package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import mu.KLogging
import java.io.File

/**
 * Will fetch and save metadata if there is none for given exchange yet
 */
class ExchangeMetadataProvider(
        exchangeMetadataFetchers: List<ExchangeMetadataFetcher>,
        private val exchangeMetadataRepository: FileExchangeMetadataRepository
) : ExchangeMetadataService {

    init {
        if (exchangeMetadataFetchers.map { it.supportedExchange }.toSet().size < exchangeMetadataFetchers.size) {
            throw IllegalStateException("Provided fetchers are invalid, there are duplicated ones")
        }
    }

    companion object : KLogging()

    override fun getMetadata(exchangeName: String): ExchangeMetadata {
        return getAndSaveExchangeMetadata(SupportedExchange.fromExchangeName(exchangeName))
    }

    override fun getMetadata(exchangeName: String, currencyPair: CurrencyPair): CurrencyPairMetadata {
        return getAndSaveExchangeMetadata(SupportedExchange.fromExchangeName(exchangeName)).currencyPairMetadata.getValue(currencyPair)
    }

    private val fetchersMap = exchangeMetadataFetchers.map { it.supportedExchange to it }.toMap()

    fun getAndSaveExchangeMetadata(supportedExchange: SupportedExchange): ExchangeMetadata {
        logger.info { "Getting $supportedExchange metadata" }
        val exchangeMetadata = exchangeMetadataRepository.getLatestExchangeMetadata(supportedExchange)
        return if (exchangeMetadata != null) {
            exchangeMetadata
        } else {
            logger.info { "Fetching $supportedExchange exchange metadata" }
            val (xchangeMetadataJson, freshExchangeMetadata) = fetchersMap.getValue(supportedExchange).fetchExchangeMetadata()
            exchangeMetadataRepository.saveExchangeMetadata(supportedExchange, freshExchangeMetadata, xchangeMetadataJson)
            return freshExchangeMetadata
        }
    }

    fun getAndSaveXchangeMetadataFile(supportedExchange: SupportedExchange): File {
        logger.info { "Getting $supportedExchange xchange metadata file" }
        val exchangeMetadataFile = exchangeMetadataRepository.getLatestXchangeMetadataFile(supportedExchange)
        return if (exchangeMetadataFile != null) {
            exchangeMetadataFile
        } else {
            logger.info { "Fetching $supportedExchange xchange metadata file" }
            val (xchangeMetadataJson, freshExchangeMetadata) = fetchersMap.getValue(supportedExchange).fetchExchangeMetadata()
            exchangeMetadataRepository.saveExchangeMetadata(supportedExchange, freshExchangeMetadata, xchangeMetadataJson)
            return exchangeMetadataRepository.getLatestXchangeMetadataFile(supportedExchange)
                    ?: throw IllegalStateException("Something went wrong. $supportedExchange exchange metadata was just fetched, but it's not accessible")
        }
    }

}
