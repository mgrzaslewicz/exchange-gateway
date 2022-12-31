package automate.profit.autocoin.spi.exchange.metadata

import java.math.BigDecimal

interface CurrencyPairMetadata {
    val amountScale: Int
    val priceScale: Int
    val minimumAmount: BigDecimal
    val maximumAmount: BigDecimal
    val minimumOrderValue: BigDecimal
    val maximumPriceMultiplierUp: BigDecimal
    val maximumPriceMultiplierDown: BigDecimal

    /**
     * Buy fee that exchange is going to add to buy amount at exchange side.
     * That means the final amount of counter currency needed would be bigger if fee not applied before creating order
     */
    val buyFeeMultiplier: BigDecimal
    val transactionFeeRanges: FeeRanges
}
