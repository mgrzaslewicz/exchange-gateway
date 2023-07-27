package com.autocoin.exchangegateway.api.exchange

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.ExchangeWithErrorMessage as SpiExchangeWithErrorMessage

data class ExchangeWithErrorMessage(
    override val exchange: Exchange,
    override val errorMessage: String?,
) : SpiExchangeWithErrorMessage

