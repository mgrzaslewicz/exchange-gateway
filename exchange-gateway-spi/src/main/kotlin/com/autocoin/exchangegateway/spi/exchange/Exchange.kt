package com.autocoin.exchangegateway.spi.exchange


interface Exchange {
    val exchangeName: ExchangeName
    val comments: List<String>
}
