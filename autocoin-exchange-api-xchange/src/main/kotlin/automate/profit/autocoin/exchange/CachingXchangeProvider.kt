package automate.profit.autocoin.exchange

import automate.profit.autocoin.exchange.peruser.md5
import mu.KLogging
import org.knowm.xchange.Exchange
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.ExchangeSpecification

class XchangeFactoryWrapper(
    private val xchangeFactory: ExchangeFactory = ExchangeFactory.INSTANCE
) { // wrap in class to make it testable as original xchange factory is an enum

    fun createExchange(exchangeSpecification: ExchangeSpecification) = xchangeFactory.createExchange(exchangeSpecification)
}

class CachingXchangeProvider(
    private val xchangeSpecificationApiKeyAssigner: XchangeSpecificationApiKeyAssigner,
    private val xchangeFactoryWrapper: XchangeFactoryWrapper
) {

    private companion object : KLogging()

    private val xchangesCache = mutableMapOf<String, Exchange>()

    fun getXchange(exchangeName: SupportedExchange, publicKey: String, secretKey: String, userName: String?, exchangeSpecificKeyParameters: Map<String, String>?): Exchange {
        val exchangeSpec = ExchangeSpecification(exchangeName.toXchangeJavaClass())
        xchangeSpecificationApiKeyAssigner.assignKeys(exchangeName, exchangeSpec, publicKey, secretKey, userName, exchangeSpecificKeyParameters)
        return getXchange(exchangeName, exchangeSpec)

    }

    fun getXchange(supportedExchange: SupportedExchange, exchangeSpec: ExchangeSpecification): Exchange {
        val cacheKey = "$supportedExchange:${exchangeSpec.apiKey.md5()}:${exchangeSpec.secretKey.md5()}"
        val keyTruncated = cacheKey.replaceRange(supportedExchange.toString().length + 4, cacheKey.length - 3, "...")
        if (xchangesCache.containsKey(cacheKey)) {
            logger.debug { "Using cached exchange for key $keyTruncated" }
        } else {
            logger.info { "[$supportedExchange] Creating exchange for key '$keyTruncated' and userName '${exchangeSpec.userName}'" }
            // TODO when it needs to be more effective, prevent from remote init each time and change provide json file
            // It will need ExchangeMetadataService modification
            exchangeSpec.isShouldLoadRemoteMetaData = true
            val xchange = xchangeFactoryWrapper.createExchange(exchangeSpec)
            xchangesCache[cacheKey] = xchange
        }
        return xchangesCache[cacheKey]!!

    }

}