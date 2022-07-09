package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange.BITTREX
import automate.profit.autocoin.exchange.currency.CurrencyPair
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.math.BigDecimal
import java.util.*

class FileExchangeMetadataRepositoryTest {

    @TempDir
    lateinit var tempFolder: File

    private val exchangeMetadataToSave = ExchangeMetadata(
        exchange = BITTREX,
        currencyPairMetadata = mapOf(
            CurrencyPair.of("ABC/BCD") to CurrencyPairMetadata(
                amountScale = 23,
                priceScale = 2,
                minimumAmount = BigDecimal("23.4567"),
                maximumAmount = BigDecimal("101.123"),
                minimumOrderValue = BigDecimal.ZERO,
                maximumPriceMultiplierUp = BigDecimal("0.2"),
                maximumPriceMultiplierDown = BigDecimal("0.15"),
                buyFeeMultiplier = BigDecimal("0.0015"),
                transactionFeeRanges = TransactionFeeRanges(
                    makerFees = listOf(
                        TransactionFeeRange(
                            beginAmount = "0.05".toBigDecimal(),
                            feeAmount = "0.02".toBigDecimal()
                        ),
                        TransactionFeeRange(
                            beginAmount = "0.25".toBigDecimal(),
                            feeAmount = "0.01".toBigDecimal()
                        )
                    ),
                    takerFees = listOf(
                        TransactionFeeRange(
                            beginAmount = "0.05".toBigDecimal(),
                            feeAmount = "0.03".toBigDecimal()
                        ),
                        TransactionFeeRange(
                            beginAmount = "0.35".toBigDecimal(),
                            feeAmount = "0.02".toBigDecimal()
                        )
                    )
                )
            )
        ),
        currencyMetadata = mapOf(
            "ABC" to CurrencyMetadata(
                scale = 3,
                minWithdrawalAmount = "0.05".toBigDecimal(),
                withdrawalFeeAmount = "0.0001".toBigDecimal(),
                depositEnabled = true,
                withdrawalEnabled = false
            )
        ),
        debugWarnings = emptyList()
    )

    @Test
    fun shouldReturnNoMetadataWhenNothingSavedBefore() {
        // given
        val exchangeMetadataRepository = FileExchangeMetadataRepository(tempFolder) { 19 }
        // when
        val savedExchangeMetadataResult = exchangeMetadataRepository.getLatestExchangeMetadata(BITTREX)
        // then
        assertThat(savedExchangeMetadataResult.hasMetadata()).isFalse()
    }

    @Test
    fun shouldReadMetadataFromFileWhenAfterOneSave() {
        // given
        val currentTimeMillis = 19L
        val exchangeMetadataRepository = FileExchangeMetadataRepository(tempFolder) { currentTimeMillis }
        exchangeMetadataRepository.saveExchangeMetadata(BITTREX, exchangeMetadataToSave)
        // when
        val savedExchangeMetadataResult = exchangeMetadataRepository.getLatestExchangeMetadata(BITTREX)
        // then
        assertThat(exchangeMetadataToSave).isEqualTo(savedExchangeMetadataResult.exchangeMetadata)
    }


    @Test
    fun shouldReadLatestMetadataFromFileAfterTwoSaves() {
        // given
        val secondExchangeMetadataToSave = exchangeMetadataToSave.copy(currencyMetadata = emptyMap())
        val exchangeMetadataRepository = FileExchangeMetadataRepository(tempFolder)
        exchangeMetadataRepository.saveExchangeMetadata(BITTREX, exchangeMetadataToSave)
        exchangeMetadataRepository.saveExchangeMetadata(BITTREX, secondExchangeMetadataToSave)
        // when
        val savedExchangeMetadataResult = exchangeMetadataRepository.getLatestExchangeMetadata(BITTREX)
        // then
        assertThat(savedExchangeMetadataResult.exchangeMetadata).isEqualTo(secondExchangeMetadataToSave)
    }

    @Test
    fun shouldCreateMetadataFileWithProperNameAndContent() {
        // given
        val currentTimeMillis = 19L
        val currentTimeMillisAsDateTimeString = "19700101010000019"
        val exchangeMetadataRepository = FileExchangeMetadataRepository(tempFolder) { currentTimeMillis }
        // when
        exchangeMetadataRepository.saveExchangeMetadata(BITTREX, exchangeMetadataToSave)
        // then
        val savedFile = tempFolder.resolve(BITTREX.exchangeName).resolve("${BITTREX.exchangeName}_$currentTimeMillisAsDateTimeString.json")
        assertThat(savedFile).exists()
        assertThat(savedFile.readText()).isEqualTo(metadataObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchangeMetadataToSave))
    }

    @Test
    fun shouldKeepLastNBackups() {
        val timeMillisQueue = ArrayDeque<Long>().apply {
            add(1L)
            add(2L)
            add(3L)
            add(4L)
        }
        val exchangeMetadataRepository = FileExchangeMetadataRepository(tempFolder) {
            timeMillisQueue.pollFirst()
        }
        exchangeMetadataRepository.saveExchangeMetadata(BITTREX, exchangeMetadataToSave)
        exchangeMetadataRepository.saveExchangeMetadata(BITTREX, exchangeMetadataToSave)
        exchangeMetadataRepository.saveExchangeMetadata(BITTREX, exchangeMetadataToSave)
        exchangeMetadataRepository.saveExchangeMetadata(BITTREX, exchangeMetadataToSave)
        // when
        exchangeMetadataRepository.keepLastNBackups(BITTREX, 2)
        // then
        assertThat(tempFolder.resolve(BITTREX.exchangeName).resolve("${BITTREX.exchangeName}_19700101010000001.json")).doesNotExist()
        assertThat(tempFolder.resolve(BITTREX.exchangeName).resolve("${BITTREX.exchangeName}_19700101010000002.json")).doesNotExist()
        assertThat(tempFolder.resolve(BITTREX.exchangeName).resolve("${BITTREX.exchangeName}_19700101010000003.json")).exists()
        assertThat(tempFolder.resolve(BITTREX.exchangeName).resolve("${BITTREX.exchangeName}_19700101010000004.json")).exists()
    }

}
