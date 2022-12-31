package automate.profit.autocoin.spi.exchange.metadata.service

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.metadata.ExchangeMetadata

interface MetadataService {
    val exchangeName: ExchangeName
    fun getMetadata(): ExchangeMetadata
    fun refreshMetadata()
}
