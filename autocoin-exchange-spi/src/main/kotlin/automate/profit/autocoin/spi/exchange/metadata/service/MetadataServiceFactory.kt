package automate.profit.autocoin.spi.exchange.metadata.service

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import java.util.function.Supplier

interface MetadataServiceFactory {
    fun createMetadataService(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
    ): MetadataService

}
