package com.autocoin.exchangegateway.api.keyvalue

import com.autocoin.exchangegateway.spi.keyvalue.KeyValueRepository
import mu.KLogging
import java.io.File
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.function.Function

class FileKeyValueRepository<K, V>(
    private val clock: Clock,
    private val directory: File,
    private val fileExtension: String,
    private val keyToFileNamePrefix: Function<K, String>,
    private val valueSerializer: Function<V, String>,
    private val valueDeserializer: Function<String, V>,
) : KeyValueRepository<LatestVersion<V>, K, V> {
    private companion object : KLogging()

    /**
     * Avoid writing files at the same millisecond
     */
    private val saveLocks = WeakHashMap<K, Any>()

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
    private fun getCurrentDateTimeAsString() = getDateTimeAsString(clock.millis())

    private fun getDateTimeAsString(millis: Long) = dateTimeFormatter.format(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime())

    private fun getNumberFrom(fileName: String): Long {
        val exchangeNameAndDateTime = fileName.split("_", fileExtension)
        return exchangeNameAndDateTime[1].toLong()
    }

    override fun getLatestVersion(
        key: K,
    ): LatestVersion<V>? {
        val fileNamePrefix = keyToFileNamePrefix.apply(key)
        val fileName = directory.ensureDirectory().list()!!.filter {
            it.startsWith(fileNamePrefix) && it.endsWith(fileExtension)
        }.maxByOrNull { getNumberFrom(it) }
        return if (fileName != null) {
            val file = directory.resolve(fileName)
            val serializedValue = file.readText()
            val value = valueDeserializer.apply(serializedValue)
            return LatestVersion(
                file = directory.resolve(fileName).toPath(),
                value = value,
            )
        }
        else {
            null
        }
    }

    private fun File.ensureDirectory(): File {
        if (!(exists() || mkdirs())) {
            throw IllegalStateException("Could not create directory $this")
        }
        return this
    }

    override fun keepLastNVersions(
        key: K,
        maxVersions: Int,
    ) {
        logger.debug { "Keeping max $maxVersions in $directory" }
        val fileNamePrefix = keyToFileNamePrefix.apply(key)
        val allFiles = directory.ensureDirectory().list()
            ?.filter { it.startsWith(fileNamePrefix) && it.endsWith(fileExtension) }
            ?.sortedBy { getNumberFrom(it) }
        if ((allFiles?.size ?: 0) > maxVersions) {
            allFiles?.subList(0, maxVersions)
                ?.forEach { directory.resolve(it).delete() }
        }
    }

    /**
     * Saves value with file name as key + version (timestamp)
     * @return path of file in directory to which value was saved
     */
    override fun saveNewVersion(
        key: K,
        value: V,
    ): LatestVersion<V> {
        val version = getCurrentDateTimeAsString()

        val fileNamePrefix = keyToFileNamePrefix.apply(key)
        val newFileName = "${fileNamePrefix}_$version$fileExtension"
        synchronized(saveLocks) {
            saveLocks.computeIfAbsent(key) { Object() }
        }
        synchronized(saveLocks.getValue(key)) {
            val serializedValue = valueSerializer.apply(value)
            val newFile = directory.ensureDirectory().resolve(newFileName)
            newFile.createNewFile()
            newFile.writeText(serializedValue)
            return LatestVersion(
                file = newFile.toPath(),
                value = value,
            )
        }
    }

    fun toBuilder() = Builder<K, V>(
        directory = directory,
        valueSerializer = valueSerializer,
        valueDeserializer = valueDeserializer,
    )
        .clock(clock)
        .fileExtension(fileExtension)
        .keyToFileNamePrefix(keyToFileNamePrefix)

    class Builder<K, V>(
        private var directory: File,
        private var valueSerializer: Function<V, String>,
        private var valueDeserializer: Function<String, V>,
    ) {
        private var clock: Clock = Clock.systemDefaultZone()
        private var fileExtension: String = ".json"
        private var keyToFileNamePrefix: Function<K, String> = Function<K, String> { it.toString() }

        fun clock(clock: Clock) = apply { this.clock = clock }
        fun directory(directory: File) = apply { this.directory = directory }
        fun fileExtension(fileExtension: String) = apply { this.fileExtension = fileExtension }
        fun keyToFileNamePrefix(keyToFileNamePrefix: Function<K, String>) = apply { this.keyToFileNamePrefix = keyToFileNamePrefix }
        fun valueSerializer(valueSerializer: Function<V, String>) = apply { this.valueSerializer = valueSerializer }
        fun valueDeserializer(valueDeserializer: Function<String, V>) = apply { this.valueDeserializer = valueDeserializer }

        fun build(): FileKeyValueRepository<K, V> {
            return FileKeyValueRepository(
                clock = clock,
                directory = directory,
                fileExtension = fileExtension,
                keyToFileNamePrefix = keyToFileNamePrefix,
                valueSerializer = valueSerializer,
                valueDeserializer = valueDeserializer,
            )
        }
    }

}
