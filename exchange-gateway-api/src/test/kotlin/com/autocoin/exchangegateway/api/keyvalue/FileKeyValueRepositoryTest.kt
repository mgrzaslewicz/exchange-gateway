package com.autocoin.exchangegateway.api.keyvalue

import com.autocoin.exchangegateway.spi.exchange.clock.QueueClock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class FileKeyValueRepositoryTest {

    @TempDir
    lateinit var tempFolder: File

    private lateinit var tested: FileKeyValueRepository<String, BigDecimal>

    @BeforeEach
    fun setup() {
        tested = FileKeyValueRepository.Builder<String, BigDecimal>(
            directory = tempFolder,
            valueDeserializer = { BigDecimal(it) },
            valueSerializer = { it.toPlainString() },
        ).build()
    }

    @Test
    fun shouldReturnNullWhenNothingSavedBefore() {
        // when
        val latestVersion = tested.getLatestVersion(key = "test")
        // then
        assertThat(latestVersion).isNull()
    }

    @Test
    fun shouldReadLatestVersionFromFileWhenAfterOneSave() {
        // given
        tested.saveNewVersion(key = "test", value = 0.05.toBigDecimal())
        // when
        val latestVersion = tested.getLatestVersion(key = "test")
        // then
        assertThat(latestVersion).isNotNull
        assertThat(latestVersion!!.value).isEqualTo(0.05.toBigDecimal())
    }


    @Test
    fun shouldReadLatestVersionFromFileAfterTwoSaves() {
        // given
        tested.saveNewVersion(key = "test", value = 0.05.toBigDecimal())
        tested.saveNewVersion(key = "test", value = 0.06.toBigDecimal())
        // when
        val latestVersion = tested.getLatestVersion(key = "test")
        // then
        assertThat(latestVersion).isNotNull
        assertThat(latestVersion!!.value).isEqualTo(0.06.toBigDecimal())
    }

    @Test
    fun shouldCreateFileWithProperNameAndContent() {
        // given
        val currentTimeMillis = 19L
        val tested = tested.toBuilder()
            .clock(Clock.fixed(Instant.ofEpochMilli(currentTimeMillis), ZoneId.systemDefault()))
            .build()
        val currentTimeMillisAsDateTimeString = "19700101010000019"
        // when
        tested.saveNewVersion(key = "test", value = 123.45.toBigDecimal())
        // then
        val savedFile = tempFolder.resolve("test_$currentTimeMillisAsDateTimeString.json")
        assertThat(savedFile).exists()
        assertThat(savedFile.readText()).isEqualTo(123.45.toBigDecimal().toPlainString())
    }

    @Test
    fun shouldKeepLastNVersions() {
        val tested = tested.toBuilder()
            .clock(QueueClock.of(1, 2, 3, 4)).build()
        tested.saveNewVersion(key = "test", 0.01.toBigDecimal())
        tested.saveNewVersion(key = "test", 0.02.toBigDecimal())
        tested.saveNewVersion(key = "test", 0.03.toBigDecimal())
        tested.saveNewVersion(key = "test", 0.04.toBigDecimal())
        // when
        tested.keepLastNVersions(key = "test", maxVersions = 2)
        // then
        assertThat(tempFolder.resolve("test_19700101010000001.json")).doesNotExist()
        assertThat(tempFolder.resolve("test_19700101010000002.json")).doesNotExist()
        assertThat(tempFolder.resolve("test_19700101010000003.json")).exists()
        assertThat(tempFolder.resolve("test_19700101010000004.json")).exists()
    }

    @Test
    fun shouldNotRemoveOtherKeysWhenKeepLastNVersions() {
        val tested = tested.toBuilder()
            .clock(QueueClock.of(1, 2, 3))
            .build()
        tested.saveNewVersion(key = "test", 0.01.toBigDecimal())
        tested.saveNewVersion(key = "test", 0.02.toBigDecimal())
        tested.saveNewVersion(key = "test2", 0.03.toBigDecimal())
        // when
        tested.keepLastNVersions(key = "test", maxVersions = 1)
        // then
        assertThat(tempFolder.resolve("test_19700101010000001.json")).doesNotExist()
        assertThat(tempFolder.resolve("test_19700101010000002.json")).exists()
        assertThat(tempFolder.resolve("test2_19700101010000003.json")).exists()
    }

}
