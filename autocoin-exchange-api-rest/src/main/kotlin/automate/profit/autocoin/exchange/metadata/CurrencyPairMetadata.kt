package automate.profit.autocoin.exchange.metadata


fun CurrencyPairMetadata.toDto() = CurrencyPairMetadataDto(
    amountScale = amountScale,
    priceScale = priceScale,
    minimumAmount = minimumAmount.toDouble(),
    maximumAmount = maximumAmount.toDouble(),
    minimumOrderValue = minimumOrderValue.toDouble(),
    maximumPriceMultiplierUp = maximumPriceMultiplierUp.toDouble(),
    maximumPriceMultiplierDown = maximumPriceMultiplierDown.toDouble(),
    buyFeeMultiplier = buyFeeMultiplier.toDouble(),
    transactionFeeRanges = TransactionFeesRangesDto(
        takerFeeRanges = transactionFeeRanges.takerFees.map { it.toDto() },
        makerFeeRanges = transactionFeeRanges.makerFees.map { it.toDto() }
    )
)


fun TransactionFee.toDto() = TransactionFeeDto(ratio = this.rate.toDouble())

fun TransactionFeeRange.toDto() = TransactionFeeRangeDto(
    beginAmount = this.beginAmount.toDouble(),
    fee = this.fee.toDto()
)
