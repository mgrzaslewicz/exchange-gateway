package automate.profit.autocoin.exchange.peruser

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.CachingXchangeProvider
import automate.profit.autocoin.exchange.apikey.ServiceApiKeysProvider
import automate.profit.autocoin.exchange.orderbook.UserExchangeOrderBookService
import automate.profit.autocoin.exchange.orderbook.XchangeUserExchangeOrderBookService
import automate.profit.autocoin.exchange.ticker.UserExchangeTickerService
import automate.profit.autocoin.exchange.toXchangeJavaClass
import mu.KLogging
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
    fun createTickerService(
        exchangeName: String,
        publicKey: String,
        secretKey: String,
        userName: String?,
        exchangeSpecificKeyParameters: Map<String, String>?
    ): UserExchangeTickerService

    fun createOrderBookService(exchangeName: String): UserExchangeOrderBookService
    fun createOrderBookService(
        exchangeName: String,
        publicKey: String,
        secretKey: String,
        userName: String?,
        exchangeSpecificKeyParameters: Map<String, String>?
    ): UserExchangeOrderBookService

    fun createTradeService(
        exchangeName: String,
        publicKey: String,
        secretKey: String,
        userName: String?,
        exchangeSpecificKeyParameters: Map<String, String>?
    ): UserExchangeTradeService

    fun createWalletService(
        exchangeName: String,
        publicKey: String,
        secretKey: String,
        userName: String?,
        exchangeSpecificKeyParameters: Map<String, String>?
    ): UserExchangeWalletService

}


class XchangeUserExchangeServicesFactory(
    private val serviceApiKeysProvider: ServiceApiKeysProvider,
    private val cachingXchangeProvider: CachingXchangeProvider
) : UserExchangeServicesFactory {
    private companion object : KLogging()


    override fun createTradeService(
        exchangeName: String,
        publicKey: String,
        secretKey: String,
        userName: String?,
        exchangeSpecificKeyParameters: Map<String, String>?
    ): UserExchangeTradeService {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        val xchange = cachingXchangeProvider.getXchange(supportedExchange, publicKey, secretKey, userName, exchangeSpecificKeyParameters)
        return XchangeUserExchangeTradeService(exchangeName, xchange.tradeService)
    }

    override fun createWalletService(
        exchangeName: String,
        publicKey: String,
        secretKey: String,
        userName: String?,
        exchangeSpecificKeyParameters: Map<String, String>?
    ): UserExchangeWalletService {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        return XchangeUserExchangeWalletService(
            supportedExchange,
            cachingXchangeProvider.getXchange(supportedExchange, publicKey, secretKey, userName, exchangeSpecificKeyParameters).accountService
        )
    }

    override fun createTickerService(exchangeName: String): UserExchangeTickerService {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        val apiKey = serviceApiKeysProvider.getApiKeys(supportedExchange)
        return when {
            apiKey != null -> {
                createTickerService(exchangeName, apiKey.publicKey, apiKey.secretKey, apiKey.userName, apiKey.exchangeSpecificKeyParameters)
            }
            else -> {
                val exchangeSpec = ExchangeSpecification(supportedExchange.toXchangeJavaClass())
                XchangeUserExchangeTickerService(cachingXchangeProvider.getXchange(supportedExchange, exchangeSpec).marketDataService, supportedExchange)
            }
        }
    }

    override fun createTickerService(
        exchangeName: String,
        publicKey: String,
        secretKey: String,
        userName: String?,
        exchangeSpecificKeyParameters: Map<String, String>?
    ): UserExchangeTickerService {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        return XchangeUserExchangeTickerService(
            cachingXchangeProvider.getXchange(supportedExchange, publicKey, secretKey, userName, exchangeSpecificKeyParameters).marketDataService,
            supportedExchange
        )
    }

    override fun createOrderBookService(exchangeName: String): UserExchangeOrderBookService {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        val apiKey = serviceApiKeysProvider.getApiKeys(supportedExchange)
        return when {
            apiKey != null -> {
                createOrderBookService(exchangeName, apiKey.publicKey, apiKey.secretKey, apiKey.userName, apiKey.exchangeSpecificKeyParameters)
            }
            else -> {
                val exchangeSpec = ExchangeSpecification(supportedExchange.toXchangeJavaClass())
                return XchangeUserExchangeOrderBookService(cachingXchangeProvider.getXchange(supportedExchange, exchangeSpec).marketDataService, exchangeName)
            }
        }
    }

    override fun createOrderBookService(
        exchangeName: String,
        publicKey: String,
        secretKey: String,
        userName: String?,
        exchangeSpecificKeyParameters: Map<String, String>?
    ): UserExchangeOrderBookService {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        return XchangeUserExchangeOrderBookService(
            cachingXchangeProvider.getXchange(supportedExchange, publicKey, secretKey, userName, exchangeSpecificKeyParameters).marketDataService,
            exchangeName
        )
    }

}



