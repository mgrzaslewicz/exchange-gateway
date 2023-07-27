package com.autocoin.exchangegateway.spi.exchange

fun interface ExchangeProvider {
    fun getExchange(exchangeName: String): Exchange
}
