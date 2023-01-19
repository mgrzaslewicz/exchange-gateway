package com.autocoin.exchangegateway.api.exchange

import com.autocoin.exchangegateway.api.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.api.exchange.xchange.ApiKeyToCacheKeyProvider
import com.autocoin.exchangegateway.api.exchange.xchange.CachingXchangeProvider
import com.autocoin.exchangegateway.api.exchange.xchange.DefaultXchangeProvider
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.binance
import com.autocoin.exchangegateway.api.exchange.xchange.XchangeApiKeyVerifierGateway
import com.autocoin.exchangegateway.api.exchange.xchange.XchangeInstanceProvider
import com.autocoin.exchangegateway.api.exchange.xchange.XchangeInstanceWrapper
import com.autocoin.exchangegateway.api.exchange.xchange.XchangeSpecificationApiKeyAssigner
import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.knowm.xchange.Exchange
import org.knowm.xchange.ExchangeSpecification
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier as SpiApiKeySupplier

class CachingXchangeProviderTest {
    private lateinit var tested: CachingXchangeProvider<String, String>
    private lateinit var countingXchangeInstanceWrapper: CountingXchangeInstanceWrapper
    private val exchangeName = ExchangeName("exchange1")

    @BeforeEach
    fun setup() {
        countingXchangeInstanceWrapper = CountingXchangeInstanceWrapper(
            decorated = XchangeInstanceWrapper(),
        )
        tested = CachingXchangeProvider(
            apiKeyToCacheKey = object : ApiKeyToCacheKeyProvider<String, String> {
                override fun invoke(
                    exchangeName: ExchangeName,
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
        override operator fun invoke(exchangeSpecification: ExchangeSpecification): Exchange {
            invokeCount++
            return decorated(exchangeSpecification)
        }
    }

    @Test
    fun shouldUseCachedXchangeInstance() {
        // when
        tested.invoke(exchangeName = binance, apiKey = ApiKeySupplier(id = "1", supplier = null))
        tested.invoke(exchangeName = binance, apiKey = ApiKeySupplier(id = "1", supplier = null))
        // then
        assertThat(countingXchangeInstanceWrapper.invokeCount).isEqualTo(1)
    }

    @Test
    fun shouldNotUseCachedXchangeInstance() {
        // when
        tested.invoke(exchangeName = binance, apiKey = ApiKeySupplier(id = "1", supplier = null))
        tested.invoke(exchangeName = binance, apiKey = ApiKeySupplier(id = "2", supplier = null))
        // then
        assertThat(countingXchangeInstanceWrapper.invokeCount).isEqualTo(2)
    }

}
