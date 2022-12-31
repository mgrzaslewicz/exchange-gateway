package com.autocoin.exchangegateway.api.exchange

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.ExchangeWithErrorMessage as SpiExchangeWithErrorMessage

data class ExchangeWithErrorMessage(
    override val exchangeName: ExchangeName,
    override val errorMessage: String?,
) : SpiExchangeWithErrorMessage

