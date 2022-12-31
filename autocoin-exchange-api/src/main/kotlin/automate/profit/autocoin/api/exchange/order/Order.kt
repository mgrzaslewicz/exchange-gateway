package automate.profit.autocoin.api.exchange.order

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.order.OrderSide
import automate.profit.autocoin.spi.exchange.order.OrderStatus
import java.math.BigDecimal
import automate.profit.autocoin.spi.exchange.order.Order as SpiOrder

data class Order(
    override val exchangeName: ExchangeName,
    override val exchangeOrderId: String,
    override val side: OrderSide,
    override val orderedAmount: BigDecimal,
    override val filledAmount: BigDecimal?,
    override val price: BigDecimal,
    override val currencyPair: CurrencyPair,
    override val status: OrderStatus,
    override val receivedAtMillis: Long,
    override val exchangeTimestampMillis: Long?,
) : SpiOrder

