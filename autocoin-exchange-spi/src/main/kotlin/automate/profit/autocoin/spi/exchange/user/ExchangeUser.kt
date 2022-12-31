package automate.profit.autocoin.spi.exchange.user

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey

interface ExchangeUser {
    val id: String
    val exchangeName: ExchangeName
    fun getApiKey(): ApiKey?
}
