package com.autocoin.exchangegateway.spi.exchange.currency

import java.math.BigDecimal

interface CurrencyBalance {
    val currencyCode: String
    val amountAvailable: BigDecimal
    val totalAmount: BigDecimal
    val amountInOrders: BigDecimal
}
