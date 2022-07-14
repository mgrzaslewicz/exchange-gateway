package automate.profit.autocoin.keyvalue

import automate.profit.autocoin.exchange.time.SystemTimeMillisProvider
import automate.profit.autocoin.exchange.time.TestFixedTimeMillisProvider
import automate.profit.autocoin.exchange.time.TestQueueTimeMillisProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class FileKeyValueRepositoryTest {

    @TempDir
    lateinit var tempFolder: File

    private lateinit var tested: FileKeyValueRepository

    @BeforeEach
    fun setup() {
        tested = FileKeyValueRepository(timeMillisProvider = SystemTimeMillisProvider())
    }

    @Test
    fun shouldReturnNullWhenNothingSavedBefore() {
        // when
        val latestVersion = tested.getLatestVersion(tempFolder, key = "test")
        // then
        assertThat(latestVersion).isNull()
    }

    @Test
    fun shouldReadLatestVersionFromFileWhenAfterOneSave() {
        // given
        tested.saveNewVersion(directory = tempFolder, key = "test", value = "value1")
        // when
        val latestVersion = tested.getLatestVersion(directory = tempFolder, key = "test")
        // then
        assertThat(latestVersion).isNotNull
        assertThat(latestVersion!!.value).isEqualTo("value1")
    }


    @Test
    fun shouldReadLatestVersionFromFileAfterTwoSaves() {
        // given
        tested.saveNewVersion(directory = tempFolder, key = "test", value = "value1")
        tested.saveNewVersion(directory = tempFolder, key = "test", value = "value2")
        // when
        val latestVersion = tested.getLatestVersion(directory = tempFolder, key = "test")
        // then
        assertThat(latestVersion).isNotNull
        assertThat(latestVersion!!.value).isEqualTo("value2")
    }

    @Test
    fun shouldCreateFileWithProperNameAndContent() {
        // given
        val currentTimeMillis = 19L
        val tested = FileKeyValueRepository(timeMillisProvider = TestFixedTimeMillisProvider(currentTimeMillis), fileExtension = ".json")
        val currentTimeMillisAsDateTimeString = "19700101010000019"
        // when
        tested.saveNewVersion(directory = tempFolder, key = "test", value = "value1")
        // then
        val savedFile = tempFolder.resolve("test_$currentTimeMillisAsDateTimeString.json")
        assertThat(savedFile).exists()
        assertThat(savedFile.readText()).isEqualTo("value1")
    }

    @Test
    fun shouldKeepLastNVersions() {
        val tested = FileKeyValueRepository(timeMillisProvider = TestQueueTimeMillisProvider(listOf(1L, 2L, 3L, 4L)), fileExtension = ".json")
        tested.saveNewVersion(directory = tempFolder, key = "test", "value1")
        tested.saveNewVersion(directory = tempFolder, key = "test", "value2")
        tested.saveNewVersion(directory = tempFolder, key = "test", "value3")
        tested.saveNewVersion(directory = tempFolder, key = "test", "value4")
        // when
        tested.keepLastNVersions(tempFolder, key = "test", maxVersions = 2)
        // then
        assertThat(tempFolder.resolve("test_19700101010000001.json")).doesNotExist()
        assertThat(tempFolder.resolve("test_19700101010000002.json")).doesNotExist()
        assertThat(tempFolder.resolve("test_19700101010000003.json")).exists()
        assertThat(tempFolder.resolve("test_19700101010000004.json")).exists()
    }

    @Test
    fun shouldNotRemoveOtherKeysWhenKeepLastNVersions() {
        val tested = FileKeyValueRepository(timeMillisProvider = TestQueueTimeMillisProvider(listOf(1L, 2L, 3L)), fileExtension = ".json")
        tested.saveNewVersion(directory = tempFolder, key = "test", "value1")
        tested.saveNewVersion(directory = tempFolder, key = "test", "value2")
        tested.saveNewVersion(directory = tempFolder, key = "test2", "value3")
        // when
        tested.keepLastNVersions(tempFolder, key = "test", maxVersions = 1)
        // then
        assertThat(tempFolder.resolve("test_19700101010000001.json")).doesNotExist()
        assertThat(tempFolder.resolve("test_19700101010000002.json")).exists()
        assertThat(tempFolder.resolve("test2_19700101010000003.json")).exists()
    }

}
