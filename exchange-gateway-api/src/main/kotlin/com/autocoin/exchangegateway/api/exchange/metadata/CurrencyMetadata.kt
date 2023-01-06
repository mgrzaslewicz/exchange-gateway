package com.autocoin.exchangegateway.api.exchange.metadata

import java.math.BigDecimal
import com.autocoin.exchangegateway.spi.exchange.metadata.CurrencyMetadata as SpiCurrencyMetadata

data class CurrencyMetadata(
    override val scale: Int = 8,
    override val withdrawalFeeAmount: BigDecimal? = null,
    override val minWithdrawalAmount: BigDecimal? = null,
    override val withdrawalEnabled: Boolean? = null,
    override val depositEnabled: Boolean? = null,
) : SpiCurrencyMetadata {
    class Builder {
        private var scale: Int? = null
        private var withdrawalFeeAmount: BigDecimal? = null
        private var minWithdrawalAmount: BigDecimal? = null
        private var withdrawalEnabled: Boolean? = null
        private var depositEnabled: Boolean? = null

        fun scale(scale: Int) = apply { this.scale = scale }
        fun defaultScaleIfNotSet(defaultScale: Int) = apply { if (scale == null) scale = defaultScale }
        fun withdrawalFeeAmount(withdrawalFeeAmount: BigDecimal?) = apply { this.withdrawalFeeAmount = withdrawalFeeAmount }
        fun minWithdrawalAmount(minWithdrawalAmount: BigDecimal?) = apply { this.minWithdrawalAmount = minWithdrawalAmount }
        fun withdrawalEnabled(withdrawalEnabled: Boolean?) = apply { this.withdrawalEnabled = withdrawalEnabled }
        fun depositEnabled(depositEnabled: Boolean?) = apply { this.depositEnabled = depositEnabled }

        fun build() = CurrencyMetadata(
            scale = scale ?: throw IllegalStateException("scale is not set"),
            withdrawalFeeAmount = withdrawalFeeAmount,
            minWithdrawalAmount = minWithdrawalAmount,
            withdrawalEnabled = withdrawalEnabled,
            depositEnabled = depositEnabled,
        )
    }
}
