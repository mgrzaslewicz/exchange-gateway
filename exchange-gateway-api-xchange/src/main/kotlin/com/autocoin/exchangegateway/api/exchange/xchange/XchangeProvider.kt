package com.autocoin.exchangegateway.api.exchange.xchange

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import org.knowm.xchange.Exchange as XchangeExchange


interface XchangeProvider<T> {
    operator fun invoke(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): XchangeExchange
}




