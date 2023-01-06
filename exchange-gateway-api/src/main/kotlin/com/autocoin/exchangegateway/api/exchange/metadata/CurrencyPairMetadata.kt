package com.autocoin.exchangegateway.api.exchange.metadata

import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import java.math.BigDecimal
import com.autocoin.exchangegateway.spi.exchange.metadata.CurrencyPairMetadata as SpiCurrencyPairMetadata
import com.autocoin.exchangegateway.spi.exchange.metadata.FeeRanges as SpiFeeRanges

data class CurrencyPairMetadata(
    override val amountScale: Int,
    override val priceScale: Int,
    override val minimumAmount: BigDecimal,
    override val maximumAmount: BigDecimal,
    override val minimumOrderValue: BigDecimal,
    override val maximumPriceMultiplierUp: BigDecimal,
    override val maximumPriceMultiplierDown: BigDecimal,
    /**
     * Buy fee that exchange is going to add to buy amount at exchange side.
     * That means the final amount of counter currency needed would be bigger if fee not applied before creating order
     */
    override val buyFeeMultiplier: BigDecimal,
    override val transactionFeeRanges: SpiFeeRanges,
) : SpiCurrencyPairMetadata {

    class Builder(
        var currencyPair: CurrencyPair,
    ) {
        private var amountScale: Int? = null
        private var priceScale: Int? = null
        private var minimumAmount: BigDecimal? = null
        private var maximumAmount: BigDecimal? = null
        private var minimumOrderValue: BigDecimal? = null
        private var maximumPriceMultiplierUp: BigDecimal? = null
        private var maximumPriceMultiplierDown: BigDecimal? = null
        private var buyFeeMultiplier: BigDecimal? = null
        private var transactionFeeRanges: FeeRanges = FeeRanges()

        fun currencyPair(currencyPair: CurrencyPair) = apply { this.currencyPair = currencyPair }
        fun amountScale(amountScale: Int?) = apply { this.amountScale = amountScale }
        fun defaultAmountScaleIfNotSet(defaultAmountScale: Int) = apply {
            if (amountScale == null) {
                amountScale = defaultAmountScale
            }
        }

        fun priceScale(priceScale: Int?) = apply { this.priceScale = priceScale }
        fun defaultPriceScaleIfNotSet(defaultPriceScale: Int) = apply {
            if (priceScale == null) {
                priceScale = defaultPriceScale
            }
        }

        fun minimumAmount(minimumAmount: BigDecimal?) = apply { this.minimumAmount = minimumAmount }
        fun defaultMinimumAmountIfNotSet(defaultMinimumAmount: BigDecimal) = apply {
            if (minimumAmount == null) {
                minimumAmount = defaultMinimumAmount
            }
        }

        fun maximumAmount(maximumAmount: BigDecimal?) = apply { this.maximumAmount = maximumAmount }
        fun defaultMaximumAmountIfNotSet(defaultMaximumAmount: BigDecimal) = apply {
            if (maximumAmount == null) {
                maximumAmount = defaultMaximumAmount
            }
        }

        fun minimumOrderValue(minimumOrderValue: BigDecimal?) = apply { this.minimumOrderValue = minimumOrderValue }
        fun defaultMinimumOrderValueIfNotSet(defaultMinimumOrderValue: BigDecimal) = apply {
            if (minimumOrderValue == null) {
                minimumOrderValue = defaultMinimumOrderValue
            }
        }

        fun maximumPriceMultiplierUp(maximumPriceMultiplierUp: BigDecimal?) = apply { this.maximumPriceMultiplierUp = maximumPriceMultiplierUp }
        fun defaultMaximumPriceMultiplierUpIfNotSet(defaultMaximumPriceMultiplierUp: BigDecimal) = apply {
            if (maximumPriceMultiplierUp == null) {
                maximumPriceMultiplierUp = defaultMaximumPriceMultiplierUp
            }
        }

        fun maximumPriceMultiplierDown(maximumPriceMultiplierDown: BigDecimal?) = apply { this.maximumPriceMultiplierDown = maximumPriceMultiplierDown }
        fun defaultMaximumPriceMultiplierDownIfNotSet(defaultMaximumPriceMultiplierDown: BigDecimal) = apply {
            if (maximumPriceMultiplierDown == null) {
                maximumPriceMultiplierDown = defaultMaximumPriceMultiplierDown
            }
        }

        fun buyFeeMultiplier(buyFeeMultiplier: BigDecimal?) = apply { this.buyFeeMultiplier = buyFeeMultiplier }
        fun defaultBuyFeeMultiplierIfNotSet(defaultBuyFeeMultiplier: BigDecimal) = apply {
            if (buyFeeMultiplier == null) {
                buyFeeMultiplier = defaultBuyFeeMultiplier
            }
        }

        fun transactionFeeRanges(transactionFeeRanges: FeeRanges) = apply { this.transactionFeeRanges = transactionFeeRanges }

        fun build() = CurrencyPairMetadata(
            amountScale = amountScale ?: throw IllegalStateException("amountScale is not set"),
            priceScale = priceScale ?: throw IllegalStateException("priceScale is not set"),
            minimumAmount = minimumAmount ?: throw IllegalStateException("minimumAmount is not set"),
            maximumAmount = maximumAmount ?: throw IllegalStateException("maximumAmount is not set"),
            minimumOrderValue = minimumOrderValue ?: throw IllegalStateException("minimumOrderValue is not set"),
            maximumPriceMultiplierUp = maximumPriceMultiplierUp ?: throw IllegalStateException("maximumPriceMultiplierUp is not set"),
            maximumPriceMultiplierDown = maximumPriceMultiplierDown ?: throw IllegalStateException("maximumPriceMultiplierDown is not set"),
            buyFeeMultiplier = buyFeeMultiplier ?: throw IllegalStateException("buyFeeMultiplier is not set"),
            transactionFeeRanges = transactionFeeRanges,
        )
    }
}
