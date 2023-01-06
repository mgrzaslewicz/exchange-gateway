package com.autocoin.exchangegateway.dto.exchange.metadata

import com.autocoin.exchangegateway.api.exchange.metadata.FeeRange
import com.autocoin.exchangegateway.spi.exchange.metadata.FeeRange as SpiFeeRange

data class FeeRangeDto(
    val beginAmount: String,
    val feeAmount: String?,
    val feeRatio: String?,
) {
    init {
        if (feeAmount == null && feeRatio == null) {
            throw error("Both feeAmount and feeRatio are null. One of them needs to be provided")
        }
    }

    fun toFeeRange() = FeeRange(
        beginAmount = this.beginAmount.toBigDecimal(),
        feeAmount = this.feeAmount?.toBigDecimal(),
        feeRatio = this.feeRatio?.toBigDecimal(),
    )

}

fun SpiFeeRange.toDto() = FeeRangeDto(
    beginAmount = this.beginAmount.toPlainString(),
    feeAmount = this.feeAmount?.toPlainString(),
    feeRatio = this.feeRatio?.toPlainString(),
)


