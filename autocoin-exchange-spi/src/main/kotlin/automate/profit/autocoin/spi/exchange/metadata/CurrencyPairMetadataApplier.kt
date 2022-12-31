package automate.profit.autocoin.spi.exchange.metadata

import java.math.BigDecimal

interface CurrencyPairMetadataApplier {

    fun adjustSellAmount(
        baseCurrencyAmount: BigDecimal,
        price: BigDecimal,
        currencyPairMetadata: CurrencyPairMetadata,
    ): BigDecimal


    fun adjustBuyAmount(
        baseCurrencyAmount: BigDecimal,
        price: BigDecimal,
        currencyPairMetadata: CurrencyPairMetadata,
        availableCounterCurrency: BigDecimal,
    ): BigDecimal


    fun applyPriceScale(
        price: BigDecimal,
        currencyPairMetadata: CurrencyPairMetadata,
    ): BigDecimal

}
