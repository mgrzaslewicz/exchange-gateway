package com.autocoin.exchangegateway.spi.exchange.orderbook

import java.math.BigDecimal

interface OrderBookAveragePrice {
    val averagePrice: BigDecimal
    val baseCurrencyAmount: BigDecimal
}
