package automate.profit.autocoin.exchange.peruser

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.SupportedExchange.*
import automate.profit.autocoin.exchange.metadata.ExchangeMetadataProvider
import automate.profit.autocoin.exchange.order.UserExchangeOrderBookService
import automate.profit.autocoin.exchange.order.XchangeUserExchangeOrderBookService
import automate.profit.autocoin.exchange.ticker.DefaultTickerListenerRegistrar
import automate.profit.autocoin.exchange.ticker.TickerListenerRegistrar
import automate.profit.autocoin.exchange.ticker.UserExchangeTickerService
import automate.profit.autocoin.exchange.toXchangeClass
import mu.KLogging
import org.knowm.xchange.Exchange
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.utils.DigestUtils
import java.security.MessageDigest

fun String?.md5(): String {
    return if (this == null) "null" else {
        val md = MessageDigest.getInstance("MD5")
        DigestUtils.bytesToHex(md.digest(toByteArray()))
    }
}

/**
 * Creates exchange services
 * - per user exchange keys
 * - or for anonymous user when no keys given
 */
interface UserExchangeServicesFactory {
    fun createTickerService(exchangeName: String): UserExchangeTickerService
    fun createTickerService(exchangeName: String, publicKey: String, secretKey: String, userName: String?, exchangeSpecificKeyParameters: Map<String, String>?): UserExchangeTickerService

    fun createOrderBookService(exchangeName: String): UserExchangeOrderBookService
    fun createOrderBookService(exchangeName: String, publicKey: String, secretKey: String, userName: String?, exchangeSpecificKeyParameters: Map<String, String>?): UserExchangeOrderBookService

    fun createTickerListenerRegistrar(exchangeName: String): TickerListenerRegistrar
    fun createTickerListenerRegistrar(exchangeName: String, publicKey: String, secretKey: String, userName: String?, exchangeSpecificKeyParameters: Map<String, String>?): TickerListenerRegistrar

    fun createTradeService(exchangeName: String, publicKey: String, secretKey: String, userName: String?, exchangeSpecificKeyParameters: Map<String, String>?): UserExchangeTradeService

    fun createWalletService(exchangeName: String, publicKey: String, secretKey: String, userName: String?, exchangeSpecificKeyParameters: Map<String, String>?): UserExchangeWalletService

}

class XchangeFactory { // wrap in class to make it testable as original xchange factory is an enum
    private val xchangeFactory = ExchangeFactory.INSTANCE
    fun createExchange(exchangeSpecification: ExchangeSpecification) = xchangeFactory.createExchange(exchangeSpecification)
}


class XchangeUserExchangeServicesFactory(
        private val xchangeFactory: XchangeFactory,
        private val exchangeMetadataProvider: ExchangeMetadataProvider,
        private val exchangeSpecificationVerifier: ExchangeSpecificationVerifier
) : UserExchangeServicesFactory {
    private companion object : KLogging()

    private val xchangesCache = mutableMapOf<String, Exchange>()

    override fun createTradeService(exchangeName: String, publicKey: String, secretKey: String, userName: String?, exchangeSpecificKeyParameters: Map<String, String>?): UserExchangeTradeService {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        return XchangeUserExchangeTradeService(exchangeName, getXchange(supportedExchange, publicKey, secretKey, userName, exchangeSpecificKeyParameters).tradeService)
    }

    override fun createWalletService(exchangeName: String, publicKey: String, secretKey: String, userName: String?, exchangeSpecificKeyParameters: Map<String, String>?): UserExchangeWalletService {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        return XchangeUserExchangeWalletService(supportedExchange, getXchange(supportedExchange, publicKey, secretKey, userName, exchangeSpecificKeyParameters).accountService)
    }

    override fun createTickerService(exchangeName: String): UserExchangeTickerService {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        val exchangeSpec = ExchangeSpecification(supportedExchange.toXchangeClass().java)
        return XchangeUserExchangeTickerService(getXchange(supportedExchange, exchangeSpec).marketDataService)
    }

    override fun createTickerService(exchangeName: String, publicKey: String, secretKey: String, userName: String?, exchangeSpecificKeyParameters: Map<String, String>?): UserExchangeTickerService {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        return XchangeUserExchangeTickerService(getXchange(supportedExchange, publicKey, secretKey, userName, exchangeSpecificKeyParameters).marketDataService)
    }

    override fun createOrderBookService(exchangeName: String): UserExchangeOrderBookService {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        val exchangeSpec = ExchangeSpecification(supportedExchange.toXchangeClass().java)
        return XchangeUserExchangeOrderBookService(getXchange(supportedExchange, exchangeSpec).marketDataService, exchangeName)
    }

    override fun createOrderBookService(exchangeName: String, publicKey: String, secretKey: String, userName: String?, exchangeSpecificKeyParameters: Map<String, String>?): UserExchangeOrderBookService {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        return XchangeUserExchangeOrderBookService(getXchange(supportedExchange, publicKey, secretKey, userName, exchangeSpecificKeyParameters).marketDataService, exchangeName)
    }

    override fun createTickerListenerRegistrar(exchangeName: String, publicKey: String, secretKey: String, userName: String?, exchangeSpecificKeyParameters: Map<String, String>?): TickerListenerRegistrar {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        return DefaultTickerListenerRegistrar(supportedExchange, createTickerService(exchangeName, publicKey, secretKey, userName, exchangeSpecificKeyParameters))
    }

    override fun createTickerListenerRegistrar(exchangeName: String): TickerListenerRegistrar {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        return DefaultTickerListenerRegistrar(supportedExchange, createTickerService(exchangeName))
    }

    private fun getXchange(exchangeName: SupportedExchange, publicKey: String, secretKey: String, userName: String?, exchangeSpecificKeyParameters: Map<String, String>?): Exchange {
        val exchangeSpec = ExchangeSpecification(exchangeName.toXchangeClass().java)
        assignKeys(exchangeName, exchangeSpec, publicKey, secretKey, userName, exchangeSpecificKeyParameters)
        return getXchange(exchangeName, exchangeSpec)
    }

    private fun setupMetadataInit(supportedExchange: SupportedExchange, exchangeSpec: ExchangeSpecification) {
        val xchangeMetadaFile = exchangeMetadataProvider.getAndSaveXchangeMetadataFile(supportedExchange).absolutePath
        when (supportedExchange) {
            // >> providing fresh metadata working
            // run LoadingMetadataManualTest to see which exchanges are able to load fresh metadata
            BINANCE,
            BITTREX,
            KUCOIN -> {
                exchangeSpec.isShouldLoadRemoteMetaData = false
                exchangeSpec.metaDataJsonFileOverride = xchangeMetadaFile
            }
            // << providing fresh metadata working

            // >> metadata implemented from static file only
            BITBAY, BITSTAMP -> {
                // TODO manual fetch metadata file or provide pull request to Xchange, it has only static data in bitbay.json and no remoteInit() implemented. priceScale and amountScale vary so default metadata might not be ok
            }
            POLONIEX -> {
                // priceScale and amountScale is 8 everywhere so default metadata is fine
                exchangeSpec.isShouldLoadRemoteMetaData = false
            }
            // << metadata from static file only

            // >> fetching metadata not working properly - gives nulls
            GATEIO -> {
                exchangeSpec.isShouldLoadRemoteMetaData = false
            }
            // << fetching metadata not working properly - gives nulls
        }
    }

    private fun getXchange(supportedExchange: SupportedExchange, exchangeSpec: ExchangeSpecification): Exchange {
        val cacheKey = "$supportedExchange:${exchangeSpec.apiKey}:${exchangeSpec.secretKey.md5()}"
        val keyTruncated = cacheKey.replaceRange(supportedExchange.toString().length + 4, cacheKey.length - 3, "...")
        if (xchangesCache.containsKey(cacheKey)) {
            logger.debug("Using cached exchange for key $keyTruncated")
        } else {
            logger.info("[$supportedExchange] Creating exchange for key '$keyTruncated' and userName '${exchangeSpec.userName}'")
            setupMetadataInit(supportedExchange, exchangeSpec)
            val xchange = xchangeFactory.createExchange(exchangeSpec)
            xchangesCache[cacheKey] = xchange
        }
        return xchangesCache[cacheKey]!!
    }

    private fun assignKeys(supportedExchange: SupportedExchange, exchangeSpecification: ExchangeSpecification, publicKey: String, secretKey: String, userName: String?, exchangeSpecificKeyParameters: Map<String, String>?) {
        exchangeSpecification.apiKey = publicKey.trim()
        exchangeSpecification.secretKey = secretKey.trim()
        exchangeSpecification.userName = userName
        exchangeSpecification.exchangeSpecificParameters = exchangeSpecificKeyParameters ?: emptyMap()

        if (exchangeSpecification.apiKey != publicKey) logger.warn("$supportedExchange API public key contained whitespaces, trimmed")
        if (exchangeSpecification.secretKey != secretKey) logger.warn("$supportedExchange API secret key contained whitespaces, trimmed")

        exchangeSpecificationVerifier.verifyKeys(supportedExchange, exchangeSpecification)
    }


}
