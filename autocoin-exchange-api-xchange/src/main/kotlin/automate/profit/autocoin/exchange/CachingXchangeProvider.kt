package automate.profit.autocoin.exchange

import automate.profit.autocoin.exchange.metadata.ExchangeMetadataProvider
import automate.profit.autocoin.exchange.peruser.ExchangeSpecificationVerifier
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
    private val xchangeFactoryWrapper: XchangeFactoryWrapper,
    private val exchangeMetadataProvider: ExchangeMetadataProvider
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
            setupMetadataInit(supportedExchange, exchangeSpec)
            val xchange = xchangeFactoryWrapper.createExchange(exchangeSpec)
            xchangesCache[cacheKey] = xchange
        }
        return xchangesCache[cacheKey]!!

    }

    private fun setupMetadataInit(supportedExchange: SupportedExchange, exchangeSpec: ExchangeSpecification) {
        val xchangeMetadataFile = exchangeMetadataProvider.getAndSaveXchangeMetadataFile(supportedExchange).absolutePath
        when (supportedExchange) {
            // >> providing fresh metadata working
            // run LoadingMetadataManualTest to see which exchanges are able to load fresh metadata
            SupportedExchange.BINANCE,
            SupportedExchange.BITTREX,
            SupportedExchange.KUCOIN -> {
                exchangeSpec.isShouldLoadRemoteMetaData = false
                exchangeSpec.metaDataJsonFileOverride = xchangeMetadataFile
            }
            // << providing fresh metadata working

            // >> metadata implemented from static file only
            SupportedExchange.BITBAY, SupportedExchange.BITSTAMP -> {
                // TODO manual fetch metadata file or provide pull request to Xchange, it has only static data in bitbay.json and no remoteInit() implemented. priceScale and amountScale vary so default metadata might not be ok
            }
            SupportedExchange.POLONIEX -> {
                // priceScale and amountScale is 8 everywhere so default metadata is fine
                exchangeSpec.isShouldLoadRemoteMetaData = false
            }
            // << metadata from static file only

            // >> fetching metadata not working properly - gives nulls
            SupportedExchange.GATEIO -> {
                exchangeSpec.isShouldLoadRemoteMetaData = false
            }
            // << fetching metadata not working properly - gives nulls
        }


    }
}