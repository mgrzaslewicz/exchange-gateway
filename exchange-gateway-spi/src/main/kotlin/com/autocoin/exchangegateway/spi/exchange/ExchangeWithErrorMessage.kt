package com.autocoin.exchangegateway.spi.exchange

interface ExchangeWithErrorMessage {
    val exchangeName: ExchangeName
    val errorMessage: String?
    fun hasNoError() = errorMessage == null
}
