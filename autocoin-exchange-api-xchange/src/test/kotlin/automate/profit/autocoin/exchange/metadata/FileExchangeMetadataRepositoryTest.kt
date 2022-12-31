package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange.BITTREX
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.time.SystemTimeMillisProvider
import automate.profit.autocoin.keyvalue.FileKeyValueRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.math.BigDecimal

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

    private val fileKeyValueRepository = FileKeyValueRepository(timeMillisProvider = SystemTimeMillisProvider())
    private lateinit var tested: FileExchangeMetadataRepository

    @BeforeEach
    fun setup() {
        tested = FileExchangeMetadataRepository(tempFolder, fileKeyValueRepository)
    }

    @Test
    fun shouldReturnNoMetadataWhenNothingSavedBefore() {
        // when
        val savedExchangeMetadataResult = tested.getLatestExchangeMetadata(BITTREX)
        // then
        assertThat(savedExchangeMetadataResult.hasMetadata()).isFalse
    }

    @Test
    fun shouldReadMetadataFromFileWhenAfterOneSave() {
        // given
        tested.saveExchangeMetadata(BITTREX, exchangeMetadataToSave)
        // when
        val savedExchangeMetadataResult = tested.getLatestExchangeMetadata(BITTREX)
        // then
        assertThat(exchangeMetadataToSave).isEqualTo(savedExchangeMetadataResult.exchangeMetadata)
    }


    @Test
    fun shouldReadLatestMetadataFromFileAfterTwoSaves() {
        // given
        val secondExchangeMetadataToSave = exchangeMetadataToSave.copy(currencyMetadata = emptyMap())
        tested.saveExchangeMetadata(BITTREX, exchangeMetadataToSave)
        tested.saveExchangeMetadata(BITTREX, secondExchangeMetadataToSave)
        // when
        val savedExchangeMetadataResult = tested.getLatestExchangeMetadata(BITTREX)
        // then
        assertThat(savedExchangeMetadataResult.exchangeMetadata).isEqualTo(secondExchangeMetadataToSave)
    }

    @Test
    fun shouldKeepLastNBackups() {
        val fileKeyValueRepository: FileKeyValueRepository = mock()
        val tested = FileExchangeMetadataRepository(metadataDirectory = tempFolder, fileKeyValueRepository = fileKeyValueRepository)
        // when
        tested.keepLastNBackups(BITTREX, 2)
        // then
        verify(fileKeyValueRepository).keepLastNVersions(tempFolder.resolve(BITTREX.exchangeName), BITTREX.exchangeName, 2)
    }

}
