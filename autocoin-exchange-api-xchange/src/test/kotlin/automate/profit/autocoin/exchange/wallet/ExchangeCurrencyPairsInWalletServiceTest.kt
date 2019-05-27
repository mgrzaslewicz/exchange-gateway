package automate.profit.autocoin.exchange.wallet

import automate.profit.autocoin.exchange.currency.CurrencyBalance
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.metadata.CurrencyMetadata
import automate.profit.autocoin.exchange.metadata.CurrencyPairMetadata
import automate.profit.autocoin.exchange.metadata.ExchangeMetadata
import automate.profit.autocoin.exchange.metadata.ExchangeMetadataService
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.math.BigDecimal


@RunWith(MockitoJUnitRunner::class)
class ExchangeCurrencyPairsInWalletServiceTest {

    private companion object : KLogging()

    private val numberDoesNotMatter = BigDecimal("4.03")
    private val currencyPairMetadataDoesNotMatter = CurrencyPairMetadata(
            amountScale = numberDoesNotMatter.scale(),
            priceScale = numberDoesNotMatter.scale(),
            minimumAmount = numberDoesNotMatter,
            maximumAmount = numberDoesNotMatter,
            minimumOrderValue = numberDoesNotMatter,
            maximumPriceMultiplierDown = numberDoesNotMatter,
            maximumPriceMultiplierUp = numberDoesNotMatter,
            buyFeeMultiplier = numberDoesNotMatter
    )
    private val exchangeMetadata = ExchangeMetadata(
            currencyPairMetadata = mapOf(
                    CurrencyPair.of("ETH/BTC") to currencyPairMetadataDoesNotMatter,
                    CurrencyPair.of("THETA/ETH") to currencyPairMetadataDoesNotMatter,
                    CurrencyPair.of("XRP/BTC") to currencyPairMetadataDoesNotMatter,
                    CurrencyPair.of("XRP/ETH") to currencyPairMetadataDoesNotMatter
            ),
            currencyMetadata = mapOf(
                    "ETH" to CurrencyMetadata(numberDoesNotMatter.scale()),
                    "BTC" to CurrencyMetadata(numberDoesNotMatter.scale()),
                    "THETA" to CurrencyMetadata(numberDoesNotMatter.scale()),
                    "XRP" to CurrencyMetadata(numberDoesNotMatter.scale())
            )
    )

    @Test
    fun shouldGenerateCurrencyPairsInWallet() {
        // given
        val currencyBalancesInWallet = listOf("ETH", "THETA", "XRP").map { toCurrencyBalance(it) }

        val exchangeMetadataService = mock<ExchangeMetadataService>().apply {
            whenever(this.getMetadata("binance")).thenReturn(exchangeMetadata)
        }
        val exchangeWalletService = mock<XchangeExchangeWalletService>().apply {
            whenever(this.getCurrencyBalances("binance", "external-exchange-user-1")).thenReturn(currencyBalancesInWallet)
        }
        val currencyPairsInWalletService = DefaultExchangeCurrencyPairsInWalletService(exchangeMetadataService, exchangeWalletService)
        // when
        val currencyPairs = currencyPairsInWalletService.generateFromWalletIfGivenEmpty("binance", "external-exchange-user-1", emptyList())
        // then
        assertThat(currencyPairs).containsOnly(
                CurrencyPair.of("THETA/ETH"),
                CurrencyPair.of("XRP/ETH")
        )
    }

    private fun toCurrencyBalance(currencyCode: String) = CurrencyBalance(
            currencyCode = currencyCode,
            available = numberDoesNotMatter,
            total = numberDoesNotMatter,
            frozen = numberDoesNotMatter
    )

}