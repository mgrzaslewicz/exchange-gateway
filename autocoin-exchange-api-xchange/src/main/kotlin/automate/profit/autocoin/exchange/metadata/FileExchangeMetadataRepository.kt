package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KLogging
import java.io.File
import java.io.IOException

fun ExchangeMetadata.asJson() = metadataObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)

private class CurrencyPairDeserializer : KeyDeserializer() {
    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserializeKey(key: String, ctxt: DeserializationContext): CurrencyPair {
        return CurrencyPair.of(key)
    }
}

private val exchangeMetadataModule = SimpleModule().apply {
    addKeyDeserializer(CurrencyPair::class.java, CurrencyPairDeserializer())
}

fun exchangeMetadataFromJson(json: String): ExchangeMetadata = metadataObjectMapper.readValue(json, ExchangeMetadata::class.java)

internal val metadataObjectMapper = jacksonObjectMapper()
        .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true).apply {
            registerModule(exchangeMetadataModule)
        }

class FileExchangeMetadataRepository(
        private val metadataDirectory: File,
        private val currentTimeMillis: () -> Long = System::currentTimeMillis
) {
    companion object : KLogging()

    fun saveExchangeMetadata(supportedExchange: SupportedExchange, exchangeMetadata: ExchangeMetadata, xchangeMetadataJson: XchangeMetadataJson) {
        logger.info { "Saving $supportedExchange metadata" }
        val exchangeDirectory = getOrCreateDirectory(supportedExchange)
        val currentTimeMillis = currentTimeMillis()

        val newMetadataFileName = "${supportedExchange.exchangeName}_$currentTimeMillis.json"
        val newXchangeMetadataFileName = "${supportedExchange.exchangeName}-xchange_$currentTimeMillis.json"
        val newMetadataFile = exchangeDirectory.resolve(newMetadataFileName)
        val newXchangeMetadataFile = exchangeDirectory.resolve(newXchangeMetadataFileName)

        logger.info { "Writing $supportedExchange metadata to file ${newMetadataFile.absolutePath}" }
        newMetadataFile.writeText(exchangeMetadata.asJson())
        logger.info { "Writing $supportedExchange xchange metadata to file ${newXchangeMetadataFile.absolutePath}" }
        newXchangeMetadataFile.writeText(xchangeMetadataJson.json)
    }

    fun getLatestExchangeMetadata(supportedExchange: SupportedExchange): ExchangeMetadata? {
        val exchangeDirectory = getOrCreateDirectory(supportedExchange)
        val latestMetadataFileName = exchangeDirectory
                .list()
                .filter { !it.contains("-xchange") && it.contains(supportedExchange.exchangeName) }
                .sortedByDescending { getMillisFromName(it) }.firstOrNull()
        return if (latestMetadataFileName != null) {
            logger.info { "Found $supportedExchange metadata file $latestMetadataFileName" }
            val latestMetadataFile = exchangeDirectory.resolve(latestMetadataFileName)
            return exchangeMetadataFromJson(latestMetadataFile.readText())
        } else null
    }

    fun getLatestXchangeMetadataFile(supportedExchange: SupportedExchange): File? {
        val exchangeDirectory = getOrCreateDirectory(supportedExchange)
        val latestMetadataFileName = exchangeDirectory
                .list()
                .filter { it.contains("${supportedExchange.exchangeName}-xchange") }
                .sortedByDescending { getMillisFromName(it) }.firstOrNull()
        return if (latestMetadataFileName != null) {
            logger.info { "Found $supportedExchange xchange metadata file $latestMetadataFileName" }
            exchangeDirectory.resolve(latestMetadataFileName)
        } else null
    }

    /**
     * bittrex_12345.json -> 12345
     * bittrex-xchange_12345.json -> 12345
     */
    private fun getMillisFromName(fileName: String): Long {
        val exchangeNameAndMillis = fileName.split("_", ".json")
        return exchangeNameAndMillis[1].toLong()
    }

    private fun getOrCreateDirectory(supportedExchange: SupportedExchange): File {
        val result = metadataDirectory.resolve(supportedExchange.exchangeName)
        if (!result.exists()) {
            if (!result.mkdirs()) {
                throw IllegalStateException("Could not create directory for $supportedExchange metadata")
            }
        }
        return result
    }

}
