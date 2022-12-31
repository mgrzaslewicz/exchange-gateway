package automate.profit.autocoin.api.exchange.currency

import automate.profit.autocoin.spi.exchange.currency.CurrencyBalance as SpiCurrencyBalance
import java.math.BigDecimal

data class CurrencyBalance(
    override val currencyCode: String,
    override val amountAvailable: BigDecimal,
    override val totalAmount: BigDecimal,
    override val amountInOrders: BigDecimal,
): SpiCurrencyBalance
