package automate.profit.autocoin.api.exchange.orderbook

import java.math.BigDecimal
import automate.profit.autocoin.spi.exchange.orderbook.OrderBookAveragePrice as SpiOrderBookAveragePrice

data class OrderBookAveragePrice(
    override val averagePrice: BigDecimal,
    override val baseCurrencyAmount: BigDecimal,
) : SpiOrderBookAveragePrice
