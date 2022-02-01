package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.exchange.currency.CurrencyPair

data class CurrencyPairMetadataDto(
    val amountScale: Int,
    val priceScale: Int,
    val minimumAmount: Double,
    val maximumAmount: Double,
    val minimumOrderValue: Double,
    val maximumPriceMultiplierUp: Double,
    val maximumPriceMultiplierDown: Double,
    val buyFeeMultiplier: Double,
    val transactionFeeRanges: TransactionFeesRangesDto
) {
    fun toCurrencyPairMetadata() = CurrencyPairMetadata(
        amountScale = amountScale,
        priceScale = priceScale,
        minimumAmount = minimumAmount.toBigDecimal(),
        maximumAmount = maximumAmount.toBigDecimal(),
        minimumOrderValue = minimumOrderValue.toBigDecimal(),
        maximumPriceMultiplierUp = maximumPriceMultiplierUp.toBigDecimal(),
        maximumPriceMultiplierDown = maximumPriceMultiplierDown.toBigDecimal(),
        buyFeeMultiplier = buyFeeMultiplier.toBigDecimal(),
        transactionFeeRanges = TransactionFeeRanges(
            makerFees = transactionFeeRanges.makerFeeRanges.map { it.toTransactionFeeRange() },
            takerFees = transactionFeeRanges.takerFeeRanges.map { it.toTransactionFeeRange() }
        )
    )
}


fun TransactionFeeRangeDto.toTransactionFeeRange() = TransactionFeeRange(
    beginAmount = this.beginAmount.toBigDecimal(),
    feeAmount = this.feeAmount?.toBigDecimal(),
    feeRatio = this.feeRatio?.toBigDecimal()
)

data class TransactionFeesRangesDto(
    val makerFeeRanges: List<TransactionFeeRangeDto>,
    val takerFeeRanges: List<TransactionFeeRangeDto>
)

data class TransactionFeeRangeDto(
    val beginAmount: Double,
    val feeAmount: Double?,
    val feeRatio: Double?
) {
    init {
        if (feeAmount == null && feeRatio == null) {
            throw error("Both feeAmount and feeRatio are null. One of them needs to be provided")
        }
    }
}

data class CurrencyMetadataDto(
    val scale: Int,
    val withdrawalFeeAmount: Double?,
    val minWithdrawalAmount: Double?,
    val withdrawalEnabled: Boolean?,
    val depositEnabled: Boolean?,
) {
    fun toCurrencyMetadata() = CurrencyMetadata(
        scale = scale,
        withdrawalFeeAmount = withdrawalFeeAmount?.toBigDecimal(),
        minWithdrawalAmount = minWithdrawalAmount?.toBigDecimal(),
        withdrawalEnabled = withdrawalEnabled,
        depositEnabled = depositEnabled,
    )
}

data class ExchangeMetadataDto(
    val currencyPairMetadata: Map<CurrencyPair, CurrencyPairMetadataDto>,
    val currencyMetadata: Map<String, CurrencyMetadataDto>,
    val debugWarnings: List<String>
) {
    fun toExchangeMetadata() = ExchangeMetadata(
        currencyPairMetadata = currencyPairMetadata.mapValues { it.value.toCurrencyPairMetadata() },
        currencyMetadata = currencyMetadata.mapValues { it.value.toCurrencyMetadata() },
        debugWarnings = debugWarnings
    )
}

fun ExchangeMetadata.toDto(includeDebugWarnings: Boolean) = ExchangeMetadataDto(
    currencyMetadata = currencyMetadata.mapValues { it.value.toDto() },
    currencyPairMetadata = currencyPairMetadata.mapValues { it.value.toDto() },
    debugWarnings = if (includeDebugWarnings) this.debugWarnings else emptyList()
)


