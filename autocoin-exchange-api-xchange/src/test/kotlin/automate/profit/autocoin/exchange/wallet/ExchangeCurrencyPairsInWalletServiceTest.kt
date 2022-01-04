package automate.profit.autocoin.exchange.wallet

import automate.profit.autocoin.exchange.currency.CurrencyBalance
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.metadata.*
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal


@ExtendWith(MockitoExtension::class)
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
        buyFeeMultiplier = numberDoesNotMatter,
        transactionFeeRanges = TransactionFeeRanges(
            makerFees = listOf(
                TransactionFeeRange(
                    beginAmount = "0.05".toBigDecimal(),
                    fee = TransactionFee(rate = "0.02".toBigDecimal())
                ),
                TransactionFeeRange(
                    beginAmount = "0.25".toBigDecimal(),
                    fee = TransactionFee(rate = "0.01".toBigDecimal())
                )
            ),
            takerFees = listOf(
                TransactionFeeRange(
                    beginAmount = "0.05".toBigDecimal(),
                    fee = TransactionFee(rate = "0.03".toBigDecimal())
                ),
                TransactionFeeRange(
                    beginAmount = "0.35".toBigDecimal(),
                    fee = TransactionFee(rate = "0.02".toBigDecimal())
                )
            )
        )
    )
    private val exchangeMetadata = ExchangeMetadata(
        currencyPairMetadata = mapOf(
            CurrencyPair.of("ETH/BTC") to currencyPairMetadataDoesNotMatter,
            CurrencyPair.of("THETA/ETH") to currencyPairMetadataDoesNotMatter,
            CurrencyPair.of("XRP/BTC") to currencyPairMetadataDoesNotMatter,
            CurrencyPair.of("XRP/ETH") to currencyPairMetadataDoesNotMatter
        ),
        currencyMetadata = mapOf(
            "ETH" to CurrencyMetadata(
                scale = numberDoesNotMatter.scale(),
                minWithdrawalAmount = "0.05".toBigDecimal(),
                withdrawalFee = "0.0001".toBigDecimal()
            ),
            "BTC" to CurrencyMetadata(
                scale = numberDoesNotMatter.scale(),
                minWithdrawalAmount = "0.06".toBigDecimal(),
                withdrawalFee = "0.0002".toBigDecimal()
            ),
            "THETA" to CurrencyMetadata(
                scale = numberDoesNotMatter.scale(),
                minWithdrawalAmount = "0.07".toBigDecimal(),
                withdrawalFee = "0.0003".toBigDecimal()
            ),
            "XRP" to CurrencyMetadata(
                scale = numberDoesNotMatter.scale(),
                minWithdrawalAmount = "0.08".toBigDecimal(),
                withdrawalFee = "0.0004".toBigDecimal()
            )
        ),
        debugWarnings = emptyList()
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
