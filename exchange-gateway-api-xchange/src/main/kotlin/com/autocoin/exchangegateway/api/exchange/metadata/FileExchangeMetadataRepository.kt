package com.autocoin.exchangegateway.api.exchange.metadata

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.keyvalue.FileKeyValueRepository
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
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair as SpiCurrencyPair
import com.autocoin.exchangegateway.spi.exchange.metadata.ExchangeMetadata as SpiExchangeMetadata

fun SpiExchangeMetadata.asJson() = metadataObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)

private class CurrencyPairDeserializer : KeyDeserializer() {
    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserializeKey(
        key: String,
        ctxt: DeserializationContext,
    ): SpiCurrencyPair {
        return CurrencyPair.of(key)
    }
}

private val exchangeMetadataModule = SimpleModule().apply {
    addKeyDeserializer(CurrencyPair::class.java, CurrencyPairDeserializer())
}

fun exchangeMetadataFromJson(json: String): ExchangeMetadata = metadataObjectMapper.readValue(json, ExchangeMetadata::class.java)

data class ExchangeMetadataResult(
    val exchangeMetadata: ExchangeMetadata? = null,
    val exception: Exception? = null,
) {
    fun hasMetadata() = exchangeMetadata != null
    fun hasException() = exception != null
}

internal val metadataObjectMapper = jacksonObjectMapper().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true).apply {
    registerModule(exchangeMetadataModule)
}

interface ExchangeMetadataRepository {
    fun getLatestExchangeMetadata(exchangeName: ExchangeName): ExchangeMetadataResult
    fun saveExchangeMetadata(
        exchangeName: ExchangeName,
        exchangeMetadata: SpiExchangeMetadata,
    )

    fun keepLastNBackups(
        exchangeName: ExchangeName,
        maxBackups: Int,
    )
}

class FileExchangeMetadataRepository(
    private val metadataDirectory: File,
    private val fileKeyValueRepository: FileKeyValueRepository,
) : ExchangeMetadataRepository {
    private companion object : KLogging()

    override fun saveExchangeMetadata(
        exchangeName: ExchangeName,
        exchangeMetadata: SpiExchangeMetadata,
    ) {
        logger.info { "[$exchangeName] Saving exchange metadata" }
        val newVersion = fileKeyValueRepository.saveNewVersion(
            directory = exchangeName.directory(),
            key = exchangeName.value,
            value = exchangeMetadata.asJson(),
        )
        logger.info { "[$exchangeName] Exchange metadata saved to ${newVersion.absolutePathString()}" }
    }

    override fun getLatestExchangeMetadata(exchangeName: ExchangeName): ExchangeMetadataResult {
        val latestMetadataVersion = fileKeyValueRepository.getLatestVersion(directory = exchangeName.directory(), key = exchangeName.value)
        return if (latestMetadataVersion != null) {
            val latestMetadataFile = latestMetadataVersion.file
            logger.info { "[$exchangeName] Found metadata file ${latestMetadataFile.absolutePathString()}" }
            return try {
                ExchangeMetadataResult(
                    exchangeMetadata = exchangeMetadataFromJson(latestMetadataVersion.value),
                )
            } catch (e: Exception) {
                ExchangeMetadataResult(exception = e)
            }
        }
        else ExchangeMetadataResult()
    }

    private fun ExchangeName.directory() = metadataDirectory.resolve(this.value)

    override fun keepLastNBackups(
        exchangeName: ExchangeName,
        maxBackups: Int,
    ) {
        logger.debug { "[$exchangeName] Keeping max $maxBackups exchange metadata files" }
        fileKeyValueRepository.keepLastNVersions(
            directory = exchangeName.directory(),
            key = exchangeName.value,
            maxVersions = maxBackups,
        )
    }

}
