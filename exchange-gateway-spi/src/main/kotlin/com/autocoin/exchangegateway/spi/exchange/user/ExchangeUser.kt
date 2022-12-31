package com.autocoin.exchangegateway.spi.exchange.user

import com.autocoin.exchangegateway.spi.exchange.ExchangeName

interface ExchangeUser {
    val id: String
    val exchangeName: ExchangeName
    fun getApiKey(): com.autocoin.exchangegateway.spi.exchange.apikey.ApiKey?
}
