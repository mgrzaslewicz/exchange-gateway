package com.autocoin.exchangegateway.spi.exchange

interface ExchangeWithErrorMessage {
    val exchange: Exchange
    val errorMessage: String?
    fun hasNoError() = errorMessage == null
}
