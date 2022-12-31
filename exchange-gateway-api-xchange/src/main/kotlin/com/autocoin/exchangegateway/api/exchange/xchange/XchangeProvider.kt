package com.autocoin.exchangegateway.api.exchange.xchange

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import org.knowm.xchange.Exchange


interface XchangeProvider<T> {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): Exchange
}




