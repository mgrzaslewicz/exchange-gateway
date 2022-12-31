package automate.profit.autocoin.exchange

import automate.profit.autocoin.api.exchange.ApiKeySupplier
import automate.profit.autocoin.exchange.xchange.CachingXchangeProvider
import automate.profit.autocoin.exchange.xchange.DefaultXchangeProvider
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.binance
import automate.profit.autocoin.exchange.xchange.XchangeApiKeyVerifierGateway
import automate.profit.autocoin.exchange.xchange.XchangeInstanceProvider
import automate.profit.autocoin.exchange.xchange.XchangeInstanceWrapper
import automate.profit.autocoin.exchange.xchange.XchangeSpecificationApiKeyAssigner
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
        tested.invoke(exchangeName = binance, apiKey = ApiKeySupplier(id = "1", supplier = null))
        tested.invoke(exchangeName = binance, apiKey = ApiKeySupplier(id = "1", supplier = null))
        // then
        assertThat(countingXchangeInstanceWrapper.invokeCount).isEqualTo(1)
    }

}
