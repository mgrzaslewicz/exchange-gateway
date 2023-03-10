package com.autocoin.exchangegateway.api.exchange.currency

import java.math.BigDecimal
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyBalance as SpiCurrencyBalance

data class CurrencyBalance(
    override val currencyCode: String,
    override val amountAvailable: BigDecimal,
    override val totalAmount: BigDecimal,
    override val amountInOrders: BigDecimal,
) : SpiCurrencyBalance
