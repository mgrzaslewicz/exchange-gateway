package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.keyvalue.FileKeyValueRepository
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KLogging
import java.io.File
import java.io.IOException
import kotlin.io.path.absolutePathString

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
    private val metadataDirectory: File,
    private val fileKeyValueRepository: FileKeyValueRepository,
) {
    private companion object : KLogging()

    fun saveExchangeMetadata(supportedExchange: SupportedExchange, exchangeMetadata: ExchangeMetadata) {
        logger.info { "[$supportedExchange] Saving exchange metadata" }
        val newVersion = fileKeyValueRepository.saveNewVersion(
            directory = supportedExchange.directory(),
            key = supportedExchange.exchangeName,
            value = exchangeMetadata.asJson()
        )
        logger.info { "[$supportedExchange] Exchange metadata saved to ${newVersion.absolutePathString()}" }
    }

    fun getLatestExchangeMetadata(supportedExchange: SupportedExchange): ExchangeMetadataResult {
        val latestMetadataVersion = fileKeyValueRepository.getLatestVersion(directory = supportedExchange.directory(), key = supportedExchange.exchangeName)
        return if (latestMetadataVersion != null) {
            val latestMetadataFile = latestMetadataVersion.file
            logger.info { "[$supportedExchange] Found metadata file ${latestMetadataFile.absolutePathString()}" }
            return try {
                ExchangeMetadataResult(
                    exchangeMetadata = exchangeMetadataFromJson(latestMetadataVersion.value)
                )
            } catch (e: Exception) {
                ExchangeMetadataResult(exception = e)
            }
        } else ExchangeMetadataResult()
    }

    private fun SupportedExchange.directory() = metadataDirectory.resolve(this.exchangeName)

    fun keepLastNBackups(supportedExchange: SupportedExchange, maxBackups: Int) {
        logger.debug { "[$supportedExchange] Keeping max $maxBackups exchange metadata files" }
        fileKeyValueRepository.keepLastNVersions(
            directory = supportedExchange.directory(),
            key = supportedExchange.exchangeName,
            maxVersions = maxBackups
        )
    }

}
