package com.autocoin.exchangegateway.api.exchange

import com.autocoin.exchangegateway.api.exchange.xchange.CachingXchangeProvider
import com.autocoin.exchangegateway.api.exchange.xchange.DefaultXchangeProvider
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.binance
import com.autocoin.exchangegateway.api.exchange.xchange.XchangeApiKeyVerifierGateway
import com.autocoin.exchangegateway.api.exchange.xchange.XchangeInstanceProvider
import com.autocoin.exchangegateway.api.exchange.xchange.XchangeInstanceWrapper
import com.autocoin.exchangegateway.api.exchange.xchange.XchangeSpecificationApiKeyAssigner
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.knowm.xchange.Exchange
import org.knowm.xchange.ExchangeSpecification

class CachingXchangeProviderTest {
    private lateinit var tested: CachingXchangeProvider<String>
    private lateinit var countingXchangeInstanceWrapper: CountingXchangeInstanceWrapper

    @BeforeEach
    fun setup() {
        countingXchangeInstanceWrapper = CountingXchangeInstanceWrapper(
            decorated = XchangeInstanceWrapper(),
        )
        tested = CachingXchangeProvider(
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
        tested.invoke(exchangeName = binance, apiKey = com.autocoin.exchangegateway.api.exchange.ApiKeySupplier(id = "1", supplier = null))
        tested.invoke(exchangeName = binance, apiKey = com.autocoin.exchangegateway.api.exchange.ApiKeySupplier(id = "1", supplier = null))
        // then
        assertThat(countingXchangeInstanceWrapper.invokeCount).isEqualTo(1)
    }

}
