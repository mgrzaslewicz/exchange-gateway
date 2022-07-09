package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import java.math.BigDecimal

data class TransactionFeeRange(
    val beginAmount: BigDecimal,
    val feeAmount: BigDecimal? = null,
    val feeRatio: BigDecimal? = null,
) {
    init {
        if (feeAmount == null && feeRatio == null) {
            throw error("Both feeAmount and feeRatio are null. One of them needs to be provided")
        }
    }
}

data class TransactionFeeRanges(
    val makerFees: List<TransactionFeeRange> = emptyList(),
    val takerFees: List<TransactionFeeRange> = emptyList()
) {
    private val takerFeesSortedAscending: List<TransactionFeeRange> by lazy { takerFees.sortedBy { it.beginAmount } }

    fun takerFeeRange(baseCurrencyAmount: BigDecimal): TransactionFeeRange? {
        return takerFeesSortedAscending.find { baseCurrencyAmount >= it.beginAmount }
    }
}

data class CurrencyPairMetadata(
    val amountScale: Int,
    val priceScale: Int,
    val minimumAmount: BigDecimal,
    val maximumAmount: BigDecimal,
    val minimumOrderValue: BigDecimal,
    val maximumPriceMultiplierUp: BigDecimal,
    val maximumPriceMultiplierDown: BigDecimal,
    /**
     * Buy fee that exchange is going to add to buy amount at exchange side.
     * That means the final amount of counter currency needed would be bigger if fee not applied before creating order
     */
    val buyFeeMultiplier: BigDecimal,
    val transactionFeeRanges: TransactionFeeRanges
)

data class CurrencyMetadata(
    val scale: Int = 8,
    val withdrawalFeeAmount: BigDecimal? = null,
    val minWithdrawalAmount: BigDecimal? = null,
    val withdrawalEnabled: Boolean? = null,
    val depositEnabled: Boolean? = null,
)

data class ExchangeMetadata(
    val exchange: SupportedExchange,
    val currencyPairMetadata: Map<CurrencyPair, CurrencyPairMetadata>,
    val currencyMetadata: Map<String, CurrencyMetadata>,
    val debugWarnings: List<String>
) {
    fun currencies() = currencyMetadata.keys

    fun currencyPairs() = currencyPairMetadata.keys
}
