package automate.profit.autocoin.exchange

import automate.profit.autocoin.exchange.SupportedExchange.BITTREX
import automate.profit.autocoin.exchange.peruser.XchangeMetadataFile
import mu.KLogging
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.dto.meta.ExchangeMetaData
import java.io.File


class LoadingMetadataManualTest {
    private companion object : KLogging()

    private fun getEmptyMetadataFile(): File {
        val file = File.createTempFile("empty-xchange-metadata", "json")
        file.writeText("""
{
  "currency_pairs": {
  },
  "currencies": {
  },
  "public_rate_limits": [
    {
    }
  ]
}
        """.trimIndent())
        return file
    }

    fun createExchangesUsingEmptyMetadataFile(): Map<SupportedExchange, ExchangeMetaData> {
        logger.info("Creating exchanges using empty metadata file and remote init")
        return createExchanges(getEmptyMetadataFile())
    }

    fun createExchangesUsingRemoteInit(): Map<SupportedExchange, ExchangeMetaData> {
        logger.info("Creating exchanges using remote init")
        val exchangesWithImplementedRemoteInit = SupportedExchange.values().filter { supportedExchange ->
            supportedExchange.toXchangeClass().java.declaredMethods.any { it.name == "remoteInit" }
        }
        val exchangesWithNoImplementedRemoteInit = SupportedExchange.values().toList() - exchangesWithImplementedRemoteInit
        logger.info("Exchanges that have remoteInit implemented: $exchangesWithImplementedRemoteInit")
        logger.info("Exchanges that have NO remoteInit implemented: $exchangesWithNoImplementedRemoteInit")
        return createExchanges(null, exchangesWithImplementedRemoteInit.toTypedArray())
    }

    private fun createExchanges(metadataFile: File?, supportedExchanges: Array<SupportedExchange> = SupportedExchange.values(), applySettings: (exchangeSpec: ExchangeSpecification) -> Unit = {}): Map<SupportedExchange, ExchangeMetaData> {
        return supportedExchanges
                .sortedBy { it.exchangeName }
                .mapNotNull {
                    try {
                        val exchangeSpec = ExchangeSpecification(it.toXchangeClass().java)
                        if (metadataFile != null) {
                            exchangeSpec.metaDataJsonFileOverride = metadataFile.absolutePath
                        }
                        applySettings(exchangeSpec)
                        val exchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpec)
                        it to exchange.exchangeMetaData
                    } catch (e: Exception) {
                        logger.warn("[${it.exchangeName}] Error during creating metadata", e)
                        null
                    }
                }.toMap()
    }

    fun createBittrexUsingFreshMetadataFile(): Map<SupportedExchange, ExchangeMetaData> {
        logger.info("Creating bittrex using fetched metadata file")
        return createExchanges(XchangeMetadataFile().fetchBittrexMetadataFile(), arrayOf(BITTREX)) { it.isShouldLoadRemoteMetaData = false }
    }

    fun logWhichLoadsFreshMetadataProperly(exchanges: Map<SupportedExchange, ExchangeMetaData>) {
        val canReadCurrencyAndCurrencyPairs = mutableListOf<SupportedExchange>()
        exchanges.forEach { supportedExchange, metadata ->
            val currencyPairsLoaded =
                    metadata.currencyPairs.isNotEmpty() &&
                            metadata.currencyPairs.all {
                                it.value?.priceScale != null
                            }
            val currenciesLoaded = metadata.currencies?.isNotEmpty() ?: false &&
                    metadata.currencies.all {
                        it.value?.scale != null
                    }
            logger.info("[$supportedExchange] Currency pairs loaded: $currencyPairsLoaded")
            logger.info("[$supportedExchange] Currencies loaded: $currenciesLoaded\n")
            if (currenciesLoaded && currenciesLoaded) {
                canReadCurrencyAndCurrencyPairs += supportedExchange
            }
        }
        logger.info("Exchanges which can read fresh currency pairs and currencies metadata: $canReadCurrencyAndCurrencyPairs")
    }

}

fun main() {
    val loadingMetadataTest = LoadingMetadataManualTest()

    loadingMetadataTest.logWhichLoadsFreshMetadataProperly(loadingMetadataTest.createExchangesUsingEmptyMetadataFile())

//    loadingMetadataTest.logWhichLoadsFreshMetadataProperly(loadingMetadataTest.createBittrexUsingFreshMetadataFile())

//    loadingMetadataTest.logWhichLoadsFreshMetadataProperly(loadingMetadataTest.createExchangesUsingRemoteInit())
}
