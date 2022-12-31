package automate.profit.autocoin.spi.exchange.metadata

import java.math.BigDecimal

interface FeeRange {
    val beginAmount: BigDecimal
    val feeAmount: BigDecimal?
    val feeRatio: BigDecimal?
}
