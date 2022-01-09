package automate.profit.autocoin.exchange.metadata

fun CurrencyMetadata.toDto() = CurrencyMetadataDto(
        scale = scale,
        withdrawalFeeAmount = withdrawalFeeAmount?.toDouble(),
        minWithdrawalAmount = minWithdrawalAmount?.toDouble()
)
