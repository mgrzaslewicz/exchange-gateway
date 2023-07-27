package com.autocoin.exchangegateway.api.exchange.xchange

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.Exchange as XchangeExchange

class DefaultXchangeProvider<T>(
    private val xchangeInstanceProvider: XchangeInstanceProvider,
    private val xchangeSpecificationApiKeyAssigner: XchangeSpecificationApiKeyAssigner,
) : XchangeProvider<T> {
    override operator fun invoke(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): XchangeExchange {
        val exchangeSpec = ExchangeSpecification((exchange as com.autocoin.exchangegateway.api.exchange.xchange.XchangeExchange).xchangeClass)
        if (apiKey.supplier != null) {
            xchangeSpecificationApiKeyAssigner.assignKeys(
                exchange = exchange,
                exchangeSpecification = exchangeSpec,
                apiKeySupplier = apiKey.supplier!!,
            )
        }
        // TODO provide a way to configure the exchange
        exchangeSpec.isShouldLoadRemoteMetaData = true
        val xchange = xchangeInstanceProvider(exchangeSpec)
        return xchange

    }
}
