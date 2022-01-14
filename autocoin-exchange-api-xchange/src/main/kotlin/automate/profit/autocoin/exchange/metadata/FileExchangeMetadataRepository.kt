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
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

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

data class ExchangeMetadataResult(
    val exchangeMetadata: ExchangeMetadata? = null, val exception: Exception? = null
) {
    fun hasMetadata() = exchangeMetadata != null
    fun hasException() = exception != null
}

internal val metadataObjectMapper = jacksonObjectMapper().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true).apply {
    registerModule(exchangeMetadataModule)
}

class FileExchangeMetadataRepository(
    private val metadataDirectory: File, private val currentTimeMillis: () -> Long = System::currentTimeMillis
) {
    companion object : KLogging()

    /**
     * Avoid writing files at the same millisecond
     */
    private val updateLocks = ConcurrentHashMap<SupportedExchange, ReentrantLock>()

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")

    private fun getCurrentDateTimeAsString() = getDateTimeAsString(currentTimeMillis())
    private fun getDateTimeAsString(millis: Long) = dateTimeFormatter.format(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime())

    private fun getLock(supportedExchange: SupportedExchange) = updateLocks.computeIfAbsent(supportedExchange) { ReentrantLock() }

    fun saveExchangeMetadata(supportedExchange: SupportedExchange, exchangeMetadata: ExchangeMetadata) {
        logger.info { "[$supportedExchange] Saving  metadata" }
        val exchangeDirectory = getOrCreateDirectory(supportedExchange)
        getLock(supportedExchange).lock()
        val currentDateTime = getCurrentDateTimeAsString()

        val newMetadataFileName = "${supportedExchange.exchangeName}_$currentDateTime.json"
        val newMetadataFile = exchangeDirectory.resolve(newMetadataFileName)
        try {
            logger.info { "[$supportedExchange] Writing exchange metadata to ${newMetadataFile.absolutePath}" }
            newMetadataFile.writeText(exchangeMetadata.asJson())
        } finally {
            getLock(supportedExchange).unlock()
        }
    }

    private fun getLatestMetadataFileName(exchangeDirectory: File, exchangeName: String): String? {
        return exchangeDirectory.list()!!.filter {
            it.contains(exchangeName) && it.endsWith(".json")
        }.maxByOrNull { getNumberFromName(it) }
    }

    fun getLatestExchangeMetadata(supportedExchange: SupportedExchange): ExchangeMetadataResult {
        val exchangeDirectory = getOrCreateDirectory(supportedExchange)
        getLock(supportedExchange).lock()
        val latestMetadataFileName = getLatestMetadataFileName(exchangeDirectory, supportedExchange.exchangeName)
        getLock(supportedExchange).unlock()
        return if (latestMetadataFileName != null) {
            val latestMetadataFile = exchangeDirectory.resolve(latestMetadataFileName)
            logger.info { "[$supportedExchange] Found metadata file ${latestMetadataFile.absolutePath}" }
            return try {
                ExchangeMetadataResult(
                    exchangeMetadata = exchangeMetadataFromJson(latestMetadataFile.readText())
                )
            } catch (e: Exception) {
                ExchangeMetadataResult(exception = e)
            }
        } else ExchangeMetadataResult()
    }

    /**
     * bittrex_12345.json -> 12345
     */
    private fun getNumberFromName(fileName: String): Long {
        val exchangeNameAndDateTime = fileName.split("_", ".json")
        return exchangeNameAndDateTime[1].toLong()
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

    fun keepLastNBackups(supportedExchange: SupportedExchange, maxBackups: Int) {
        logger.debug { "[$supportedExchange] Keeping max $maxBackups exchange metadata files" }
        val exchangeDirectory = getOrCreateDirectory(supportedExchange)
        val allFiles = exchangeDirectory.list()
            .filter { it.contains(supportedExchange.exchangeName) }
            .sortedBy { getNumberFromName(it) }
        if (allFiles.size > maxBackups) {
            allFiles
                .subList(0, maxBackups)
                .forEach { exchangeDirectory.resolve(it).delete() }
        }
    }

}
