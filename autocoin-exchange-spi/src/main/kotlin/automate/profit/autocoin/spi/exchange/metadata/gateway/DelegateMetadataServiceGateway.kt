package automate.profit.autocoin.spi.exchange.metadata.gateway

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.metadata.ExchangeMetadata
import automate.profit.autocoin.spi.exchange.metadata.service.MetadataService
import java.util.function.Supplier

class DelegateMetadataServiceGateway(
    private val metadataServiceGateways: Map<ExchangeName, MetadataService>,
) : MetadataServiceGateway {

    override fun refreshMetadata(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>?) {
        metadataServiceGateways.getValue(exchangeName).refreshMetadata()
    }

    override fun getMetadata(exchangeName: ExchangeName, apiKey: Supplier<ApiKey>?): ExchangeMetadata {
        return metadataServiceGateways.getValue(exchangeName).getMetadata()
    }

}
