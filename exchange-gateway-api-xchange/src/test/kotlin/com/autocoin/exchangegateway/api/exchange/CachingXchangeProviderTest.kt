package com.autocoin.exchangegateway.api.exchange

import com.autocoin.exchangegateway.api.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.api.exchange.xchange.*
import com.autocoin.exchangegateway.api.exchange.xchange.SupportedXchangeExchange.binance
import com.autocoin.exchangegateway.spi.exchange.Exchange
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.knowm.xchange.ExchangeSpecification
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier as SpiApiKeySupplier
import org.knowm.xchange.Exchange as XchangeExchange

class CachingXchangeProviderTest {
    private lateinit var tested: CachingXchangeProvider<String, String>
    private lateinit var countingXchangeInstanceWrapper: CountingXchangeInstanceWrapper

    @BeforeEach
    fun setup() {
        countingXchangeInstanceWrapper = CountingXchangeInstanceWrapper(
            decorated = XchangeInstanceWrapper(),
        )
        tested = CachingXchangeProvider(
            apiKeyToCacheKey = object : ApiKeyToCacheKeyProvider<String, String> {
                override fun invoke(
                    exchange: Exchange,
                    apiKey: SpiApiKeySupplier<String>,
                ): String {
                    return apiKey.id
                }
            },
            decorated = DefaultXchangeProvider(
                xchangeInstanceProvider = countingXchangeInstanceWrapper,
                xchangeSpecificationApiKeyAssigner = XchangeSpecificationApiKeyAssigner(
                    apiKeyVerifierGateway = XchangeApiKeyVerifierGateway(),
                ),
            ),
        )
    }

    private class CountingXchangeInstanceWrapper(private val decorated: XchangeInstanceProvider) : XchangeInstanceProvider {
        var invokeCount = 0
        override operator fun invoke(exchangeSpecification: ExchangeSpecification): XchangeExchange {
            invokeCount++
            return decorated(exchangeSpecification)
        }
    }

    @Test
    fun shouldUseCachedXchangeInstance() {
        // when
        tested.invoke(exchange = binance, apiKey = ApiKeySupplier(id = "1", supplier = null))
        tested.invoke(exchange = binance, apiKey = ApiKeySupplier(id = "1", supplier = null))
        // then
        assertThat(countingXchangeInstanceWrapper.invokeCount).isEqualTo(1)
    }

    @Test
    fun shouldNotUseCachedXchangeInstance() {
        // when
        tested.invoke(exchange = binance, apiKey = ApiKeySupplier(id = "1", supplier = null))
        tested.invoke(exchange = binance, apiKey = ApiKeySupplier(id = "2", supplier = null))
        // then
        assertThat(countingXchangeInstanceWrapper.invokeCount).isEqualTo(2)
    }

}
