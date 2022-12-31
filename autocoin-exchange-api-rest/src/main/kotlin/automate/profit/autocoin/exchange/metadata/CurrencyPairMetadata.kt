package automate.profit.autocoin.exchange.metadata


fun CurrencyPairMetadata.toDto() = CurrencyPairMetadataDto(
        amountScale = amountScale,
        priceScale = priceScale,
        minimumAmount = minimumAmount.toDouble(),
        maximumAmount = maximumAmount.toDouble(),
        minimumOrderValue = minimumOrderValue.toDouble(),
        maximumPriceMultiplierUp = maximumPriceMultiplierUp.toDouble(),
        maximumPriceMultiplierDown = maximumPriceMultiplierDown.toDouble(),
        buyFeeMultiplier = buyFeeMultiplier.toDouble()
)
