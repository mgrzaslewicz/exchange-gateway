package automate.profit.autocoin.exchange.metadata

interface ExchangeMetadataService {
    fun getMetadata(exchangeName: String): ExchangeMetadata
}
