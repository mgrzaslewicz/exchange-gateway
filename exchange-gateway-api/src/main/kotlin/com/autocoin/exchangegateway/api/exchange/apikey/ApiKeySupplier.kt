package com.autocoin.exchangegateway.api.exchange.apikey

import java.util.function.Supplier
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier as SpiApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKey as SpiApiKey

data class ApiKeySupplier<T>(
    override val id: T,
    override val supplier: Supplier<SpiApiKey>?,
) : SpiApiKeySupplier<T>
