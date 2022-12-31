package com.autocoin.exchangegateway.api.exchange.metadata

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
) : SpiCurrencyPairMetadata
