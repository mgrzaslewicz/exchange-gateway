package com.autocoin.exchangegateway.dto.exchange.metadata

import com.autocoin.exchangegateway.api.exchange.metadata.FeeRanges


data class FeeRangesDto(
    val makerFeeRanges: List<FeeRangeDto>,
    val takerFeeRanges: List<FeeRangeDto>,
) {
    fun toFeeRanges() = FeeRanges(
        makerFees = makerFeeRanges.map { it.toFeeRange() },
        takerFees = takerFeeRanges.map { it.toFeeRange() },
    )
}

fun FeeRanges.toDto() = FeeRangesDto(
    makerFeeRanges = this.makerFees.map { it.toDto() },
    takerFeeRanges = this.takerFees.map { it.toDto() },
)

