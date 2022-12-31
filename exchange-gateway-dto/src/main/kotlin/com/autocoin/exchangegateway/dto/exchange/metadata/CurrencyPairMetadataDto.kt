package com.autocoin.exchangegateway.dto.exchange.metadata

import com.autocoin.exchangegateway.api.exchange.metadata.CurrencyPairMetadata
import com.autocoin.exchangegateway.api.exchange.metadata.FeeRanges
import com.autocoin.exchangegateway.dto.SerializableToJson
import com.autocoin.exchangegateway.spi.exchange.metadata.CurrencyPairMetadata as SpiCurrencyPairMetadata

data class CurrencyPairMetadataDto(
    val amountScale: Int,
    val priceScale: Int,
    val minimumAmount: String,
    val maximumAmount: String,
    val minimumOrderValue: String,
    val maximumPriceMultiplierUp: String,
    val maximumPriceMultiplierDown: String,
    val buyFeeMultiplier: String,
    val transactionFeeRanges: FeeRangesDto,
) : SerializableToJson {
    fun toCurrencyPairMetadata() = CurrencyPairMetadata(
        amountScale = amountScale,
        priceScale = priceScale,
        minimumAmount = minimumAmount.toBigDecimal(),
        maximumAmount = maximumAmount.toBigDecimal(),
        minimumOrderValue = minimumOrderValue.toBigDecimal(),
        maximumPriceMultiplierUp = maximumPriceMultiplierUp.toBigDecimal(),
        maximumPriceMultiplierDown = maximumPriceMultiplierDown.toBigDecimal(),
        buyFeeMultiplier = buyFeeMultiplier.toBigDecimal(),
        transactionFeeRanges = FeeRanges(
            makerFees = transactionFeeRanges.makerFeeRanges.map { it.toFeeRange() },
            takerFees = transactionFeeRanges.takerFeeRanges.map { it.toFeeRange() },
        ),
    )

    override fun appendJson(builder: StringBuilder) = builder
        .append("{")
        .append("\"amountScale\":")
        .append(amountScale)
        .append(",\"priceScale\":")
        .append(priceScale)
        .append(",\"minimumAmount\":\"")
        .append(minimumAmount)
        .append("\",\"maximumAmount\":\"")
        .append(maximumAmount)
        .append("\",\"minimumOrderValue\":\"")
        .append(minimumOrderValue)
        .append("\",\"maximumPriceMultiplierUp\":\"")
        .append(maximumPriceMultiplierUp)
        .append("\",\"maximumPriceMultiplierDown\":\"")
        .append(maximumPriceMultiplierDown)
        .append("\",\"buyFeeMultiplier\":\"")
        .append(buyFeeMultiplier)
        .append("\",\"transactionFeeRanges\":")
        .append(transactionFeeRanges.toJson())
        .append("}")
}

fun SpiCurrencyPairMetadata.toDto() = CurrencyPairMetadataDto(
    amountScale = amountScale,
    priceScale = priceScale,
    minimumAmount = minimumAmount.toPlainString(),
    maximumAmount = maximumAmount.toPlainString(),
    minimumOrderValue = minimumOrderValue.toPlainString(),
    maximumPriceMultiplierUp = maximumPriceMultiplierUp.toPlainString(),
    maximumPriceMultiplierDown = maximumPriceMultiplierDown.toPlainString(),
    buyFeeMultiplier = buyFeeMultiplier.toPlainString(),
    transactionFeeRanges = FeeRangesDto(
        makerFeeRanges = transactionFeeRanges.makerFees.map { it.toDto() },
        takerFeeRanges = transactionFeeRanges.takerFees.map { it.toDto() },
    ),
)
