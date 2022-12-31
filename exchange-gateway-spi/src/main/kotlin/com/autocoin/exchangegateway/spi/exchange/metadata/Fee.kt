package com.autocoin.exchangegateway.spi.exchange.metadata

import java.math.BigDecimal


interface FeeRanges {
    val makerFees: List<FeeRange>
    val takerFees: List<FeeRange>
    fun takerFeeRange(baseCurrencyAmount: BigDecimal): FeeRange?
}
