package automate.profit.autocoin.api.exchange.metadata

import java.math.BigDecimal
import automate.profit.autocoin.spi.exchange.metadata.CurrencyMetadata as SpiCurrencyMetadata

data class CurrencyMetadata(
    override val scale: Int = 8,
    override val withdrawalFeeAmount: BigDecimal? = null,
    override val minWithdrawalAmount: BigDecimal? = null,
    override val withdrawalEnabled: Boolean? = null,
    override val depositEnabled: Boolean? = null,
) : SpiCurrencyMetadata
