package com.autocoin.exchangegateway.api.exchange.metadata

import java.math.BigDecimal
import com.autocoin.exchangegateway.spi.exchange.metadata.FeeRange as SpiFeeRange
import com.autocoin.exchangegateway.spi.exchange.metadata.FeeRanges as SpiFeeRanges

data class FeeRange(
    override val beginAmount: BigDecimal,
    override val feeAmount: BigDecimal? = null,
    override val feeRatio: BigDecimal? = null,
) : SpiFeeRange {
    init {
        if (feeAmount == null && feeRatio == null) {
            throw error("Both feeAmount and feeRatio are null. One of them needs to be provided")
        }
    }
}

data class FeeRanges(
    override val makerFees: List<SpiFeeRange> = emptyList(),
    override val takerFees: List<SpiFeeRange> = emptyList(),
) : SpiFeeRanges {
    private val takerFeesSortedAscending: List<SpiFeeRange> by lazy { takerFees.sortedBy { it.beginAmount } }

    override fun takerFeeRange(baseCurrencyAmount: BigDecimal): SpiFeeRange? {
        return takerFeesSortedAscending.find { baseCurrencyAmount >= it.beginAmount }
    }
}




