package automate.profit.autocoin.api.exchange

import automate.profit.autocoin.spi.exchange.apikey.ApiKey as SpiApiKey

data class ApiKey(
    override val publicKey: String,
    override val secretKey: String,
    override val userName: String? = null,
    override val exchangeSpecificKeyParameters: Map<String, String>? = null,
) : SpiApiKey
