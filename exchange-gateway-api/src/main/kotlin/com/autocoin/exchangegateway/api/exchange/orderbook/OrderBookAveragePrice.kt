package com.autocoin.exchangegateway.api.exchange.orderbook

import java.math.BigDecimal
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderBookAveragePrice as SpiOrderBookAveragePrice

data class OrderBookAveragePrice(
    override val averagePrice: BigDecimal,
    override val baseCurrencyAmount: BigDecimal,
) : SpiOrderBookAveragePrice
