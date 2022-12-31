package automate.profit.autocoin.spi.exchange.metadata

import java.math.BigDecimal

interface CurrencyMetadata {
    val scale: Int
    val withdrawalFeeAmount: BigDecimal?
    val minWithdrawalAmount: BigDecimal?
    val withdrawalEnabled: Boolean?
    val depositEnabled: Boolean?
}
