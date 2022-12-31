package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange.BITTREX
import automate.profit.autocoin.exchange.currency.CurrencyPair
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.math.BigDecimal

class FileExchangeMetadataRepositoryTest {

    private val tempFolder = TemporaryFolder()
    private val exchangeMetadataToSave = ExchangeMetadata(
            currencyPairMetadata = mapOf(
                    CurrencyPair.of("ABC/BCD") to CurrencyPairMetadata(
                            amountScale = 23,
                            priceScale = 2,
                            minimumAmount = BigDecimal("23.4567"),
                            maximumAmount = BigDecimal("101.123"),
                            minimumOrderValue = BigDecimal.ZERO,
                            maximumPriceMultiplierUp = BigDecimal("0.2"),
                            maximumPriceMultiplierDown = BigDecimal("0.15"),
                            buyFeeMultiplier = BigDecimal("0.0015")

                    )
            ),
            currencyMetadata = mapOf(
                    "ABC" to CurrencyMetadata(
                            scale = 3
                    )
            )
    )
    private val xchangeMetadataJson = XchangeMetadataJson("{}")

    @Before
    fun setup() {
        tempFolder.create()
    }

    @After
    fun cleanup() {
        tempFolder.delete()
    }

    @Test
    fun shouldReturnNoMetadataWhenNothingSavedBefore() {
        // given
        val exchangeMetadataRepository = FileExchangeMetadataRepository(tempFolder.root) { 19 }
        // when
        val savedExchangeMetadata = exchangeMetadataRepository.getLatestExchangeMetadata(BITTREX)
        // then
        assertThat(savedExchangeMetadata).isNull()
    }

    @Test
    fun shouldReadMetadataFromFileWhenAfterOneSave() {
        // given
        val currentTimeMillis = 19L
        val exchangeMetadataRepository = FileExchangeMetadataRepository(tempFolder.root) { currentTimeMillis }
        exchangeMetadataRepository.saveExchangeMetadata(BITTREX, exchangeMetadataToSave, xchangeMetadataJson)
        // when
        val savedExchangeMetadata = exchangeMetadataRepository.getLatestExchangeMetadata(BITTREX)
        // then
        assertThat(exchangeMetadataToSave).isEqualTo(savedExchangeMetadata)
    }


    @Test
    fun shouldReadLatestMetadataFromFileAfterTwoSaves() {
        // given
        val secondExchangeMetadataToSave = exchangeMetadataToSave.copy(currencyMetadata = emptyMap())
        val exchangeMetadataRepository = FileExchangeMetadataRepository(tempFolder.root)
        exchangeMetadataRepository.saveExchangeMetadata(BITTREX, exchangeMetadataToSave, xchangeMetadataJson)
        exchangeMetadataRepository.saveExchangeMetadata(BITTREX, secondExchangeMetadataToSave, xchangeMetadataJson)
        // when
        val savedExchangeMetadata = exchangeMetadataRepository.getLatestExchangeMetadata(BITTREX)
        // then
        assertThat(savedExchangeMetadata).isEqualTo(secondExchangeMetadataToSave)
    }

    @Test
    fun shouldCreateMetadataFileWithProperNameAndContent() {
        // given
        val currentTimeMillis = 19L
        val exchangeMetadataRepository = FileExchangeMetadataRepository(tempFolder.root) { currentTimeMillis }
        exchangeMetadataRepository.saveExchangeMetadata(BITTREX, exchangeMetadataToSave, xchangeMetadataJson)
        // when
        val savedFile = tempFolder.root.resolve(BITTREX.exchangeName).resolve("${BITTREX.exchangeName}_$currentTimeMillis.json")
        // then
        assertThat(savedFile).exists()
        assertThat(savedFile.readText()).isEqualTo(metadataObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchangeMetadataToSave))
    }

}
