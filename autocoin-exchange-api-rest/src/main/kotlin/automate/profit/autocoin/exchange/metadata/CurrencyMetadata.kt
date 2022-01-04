package automate.profit.autocoin.exchange.metadata

fun CurrencyMetadata.toDto() = CurrencyMetadataDto(
        scale = scale,
        withdrawalFee = withdrawalFee?.toDouble(),
        minWithdrawalAmount = minWithdrawalAmount?.toDouble()
)
