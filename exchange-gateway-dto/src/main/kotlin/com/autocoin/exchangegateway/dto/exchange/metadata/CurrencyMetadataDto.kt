package com.autocoin.exchangegateway.dto.exchange.metadata

import com.autocoin.exchangegateway.api.exchange.metadata.CurrencyMetadata
import com.autocoin.exchangegateway.spi.exchange.metadata.CurrencyMetadata as SpiCurrencyMetadata

data class CurrencyMetadataDto(
    val scale: Int,
    val withdrawalFeeAmount: String?,
    val minWithdrawalAmount: String?,
    val withdrawalEnabled: Boolean?,
    val depositEnabled: Boolean?,
) {
    fun toCurrencyMetadata(): SpiCurrencyMetadata = CurrencyMetadata(
        scale = scale,
        withdrawalFeeAmount = withdrawalFeeAmount?.toBigDecimal(),
        minWithdrawalAmount = minWithdrawalAmount?.toBigDecimal(),
        withdrawalEnabled = withdrawalEnabled,
        depositEnabled = depositEnabled,
    )
}

fun SpiCurrencyMetadata.toDto() = CurrencyMetadataDto(
    scale = scale,
    withdrawalFeeAmount = withdrawalFeeAmount?.toPlainString(),
    minWithdrawalAmount = minWithdrawalAmount?.toPlainString(),
    withdrawalEnabled = withdrawalEnabled,
    depositEnabled = depositEnabled,
)
