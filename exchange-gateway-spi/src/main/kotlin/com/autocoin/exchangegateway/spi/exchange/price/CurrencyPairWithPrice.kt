package com.autocoin.exchangegateway.spi.exchange.price

import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import java.math.BigDecimal

interface CurrencyPairWithPrice {
    val currencyPair: CurrencyPair
    val price: BigDecimal
}
