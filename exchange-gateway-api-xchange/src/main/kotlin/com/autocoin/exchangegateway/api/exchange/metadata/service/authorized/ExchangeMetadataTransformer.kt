package com.autocoin.exchangegateway.api.exchange.metadata.service.authorized

import com.autocoin.exchangegateway.api.exchange.metadata.ExchangeMetadata

interface ExchangeMetadataTransformer {
    operator fun invoke(exchangeMetadataBuilder: ExchangeMetadata.Builder)
}
