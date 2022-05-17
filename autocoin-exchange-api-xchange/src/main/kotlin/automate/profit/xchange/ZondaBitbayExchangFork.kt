package automate.profit.xchange

import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.bitbay.BitbayExchange

class ZondaBitbayExchangFork: BitbayExchange() {
    override fun getDefaultExchangeSpecification(): ExchangeSpecification {
        return super.getDefaultExchangeSpecification().apply {
            sslUri = "https://api.zonda.exchange/rest"
        }
    }
}
