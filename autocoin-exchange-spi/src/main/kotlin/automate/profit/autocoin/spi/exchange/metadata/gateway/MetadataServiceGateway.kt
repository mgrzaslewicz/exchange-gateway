package automate.profit.autocoin.spi.exchange.metadata.gateway

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.metadata.ExchangeMetadata
import java.util.function.Supplier

interface MetadataServiceGateway {
    fun refreshMetadata(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey?>,
    )

    fun getMetadata(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey?>,
    ): ExchangeMetadata

}
