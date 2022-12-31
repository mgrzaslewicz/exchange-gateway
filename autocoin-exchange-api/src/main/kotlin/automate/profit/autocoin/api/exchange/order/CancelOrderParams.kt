package automate.profit.autocoin.api.exchange.order

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.order.OrderSide
import automate.profit.autocoin.spi.exchange.order.CancelOrderParams as SpiCancelOrderParams

data class CancelOrderParams(
    override val exchangeName: ExchangeName,
    override val orderId: String,
    override val orderSide: OrderSide,
    override val currencyPair: CurrencyPair,
) : SpiCancelOrderParams
