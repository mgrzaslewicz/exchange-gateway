package com.autocoin.exchangegateway.api.price

import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import java.math.BigDecimal
import com.autocoin.exchangegateway.spi.exchange.price.CurrencyPairWithPrice as SpiCurrencyPairWithPrice

data class CurrencyPairWithPrice(
    override val currencyPair: CurrencyPair,
    override val price: BigDecimal,
) : SpiCurrencyPairWithPrice
