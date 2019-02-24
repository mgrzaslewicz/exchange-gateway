package automate.profit.autocoin.exchange.metadata

interface ExchangeMetadataService {
    fun getMetadata(exchangeName: String, exchangeUserId: String): ExchangeMetadata
    fun getMetadata(exchangeName: String): ExchangeMetadata
}
