package automate.profit.autocoin.exchange.peruser

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.SupportedExchange.*
import automate.profit.autocoin.exchange.metadata.DefaultUserExchangeMetadataProvider
import automate.profit.autocoin.exchange.metadata.UserExchangeMetadataProvider
import automate.profit.autocoin.exchange.metadata.metadataFromExchange
import automate.profit.autocoin.exchange.ticker.DefaultTickerListenerRegistrar
import automate.profit.autocoin.exchange.ticker.TickerListenerRegistrar
import automate.profit.autocoin.exchange.ticker.UserExchangeTickerService
import automate.profit.autocoin.exchange.toXchangeClass
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import mu.KLogging
import org.knowm.xchange.Exchange
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.bittrex.service.BittrexMarketDataServiceRaw
import org.knowm.xchange.utils.DigestUtils
import java.io.File
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
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
    fun createTickerService(exchangeName: String, publicKey: String, secretKey: String, userName: String?): UserExchangeTickerService

    fun createTickerListenerRegistrar(exchangeName: String): TickerListenerRegistrar
    fun createTickerListenerRegistrar(exchangeName: String, publicKey: String, secretKey: String, userName: String?): TickerListenerRegistrar

    fun createTradeService(exchangeName: String, publicKey: String, secretKey: String, userName: String?): UserExchangeTradeService

    fun createWalletService(exchangeName: String, publicKey: String, secretKey: String, userName: String?): UserExchangeWalletService

    fun createMetadataProvider(exchangeName: String, publicKey: String, secretKey: String, userName: String?): UserExchangeMetadataProvider
    fun createMetadataProvider(exchangeName: String): UserExchangeMetadataProvider
}

class XchangeFactory { // wrap in class to make it testable as original xchange factory is an enum
    private val xchangeFactory = ExchangeFactory.INSTANCE
    fun createExchange(exchangeSpecification: ExchangeSpecification) = xchangeFactory.createExchange(exchangeSpecification)
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CurrencyPairJsonNode(
        @JsonProperty("price_scale")
        val priceScale: Int,
        @JsonProperty("min_amount")
        val minAmount: BigDecimal,
        val tradingFee: Double? = null
)

data class CurrencyJsonNode(
        @JsonProperty("scale")
        val scale: Int,
        @JsonProperty("withdrawal_fee")
        val withdrawalFee: Double
)

data class PublicRateLimitJson(
        @JsonProperty("calls")
        val calls: Int,
        @JsonProperty("time_span")
        val timeSpan: Int,
        @JsonProperty("time_unit")
        val timeUnit: String
)

data class XchangeMetadataJson(
        val currencyPairs: Map<String, CurrencyPairJsonNode>,
        val currencies: Map<String, CurrencyJsonNode>,
        val publicRateLimits: List<PublicRateLimitJson>
)

class XchangeMetadataFile {
    private var emptyMetadataFile = File("")
    private val mapper = ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .writerWithDefaultPrettyPrinter()

    fun write(exchangeName: String, metadataJson: XchangeMetadataJson): File {
        val content = mapper.writeValueAsString(metadataJson)
        val currentTimeMs = System.currentTimeMillis()
        return File.createTempFile("$exchangeName-$currentTimeMs", "json").apply {
            writeText(content)
        }
    }

    fun getEmptyMetadataFile(): File {
        if (!emptyMetadataFile.exists()) {
            emptyMetadataFile = File.createTempFile("empty-xchange-metadata", "json")
            emptyMetadataFile.writeText("""
{
  "currency_pairs": {
  },
  "currencies": {
  },
  "public_rate_limits": [
    {
    }
  ]
}
        """.trimIndent())
        }
        return emptyMetadataFile
    }

    fun fetchBittrexMetadataFile(): File {
        val bittrexMetadata = fetchBittrexMetadata()
        return write("bittrex", bittrexMetadata)
    }

    private fun fetchBittrexMetadata(): XchangeMetadataJson {
        val exchangeSpec = ExchangeSpecification(SupportedExchange.BITTREX.toXchangeClass().java)
        val mathContext = MathContext(8, RoundingMode.HALF_UP)
        val bittrexExchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpec)
        val bittrexMarketDataService = bittrexExchange.marketDataService as BittrexMarketDataServiceRaw

        val currencyPairs = bittrexMarketDataService.bittrexSymbols.map {
            "${it.marketCurrency}/${it.baseCurrency}" to CurrencyPairJsonNode(
                    priceScale = 8,
                    minAmount = BigDecimal(it.minTradeSize.toDouble(), mathContext)
            )
        }.toMap()

        val currencies = bittrexMarketDataService.bittrexCurrencies.map {
            it.currency to CurrencyJsonNode(
                    scale = 8,
                    withdrawalFee = it.txFee.toDouble()
            )
        }.toMap()

        val metadataRootNode = XchangeMetadataJson(
                currencyPairs = currencyPairs,
                currencies = currencies,
                publicRateLimits = listOf(PublicRateLimitJson(
                        calls = 3,
                        timeSpan = 1,
                        timeUnit = "SECONDS"
                ))
        )
        return metadataRootNode
    }

}

class XchangeUserExchangeServicesFactory(private val xchangeFactory: XchangeFactory, private val xchangeMetadataFile: XchangeMetadataFile) : UserExchangeServicesFactory {
    private companion object : KLogging()

    private val xchangesCache = mutableMapOf<String, Exchange>()

    override fun createTradeService(exchangeName: String, publicKey: String, secretKey: String, userName: String?): UserExchangeTradeService {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        return XchangeUserExchangeTradeService(exchangeName, getXchange(supportedExchange, publicKey, secretKey, userName).tradeService)
    }

    override fun createMetadataProvider(exchangeName: String, publicKey: String, secretKey: String, userName: String?): UserExchangeMetadataProvider {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        return DefaultUserExchangeMetadataProvider(exchangeName, metadataFromExchange(supportedExchange, getXchange(supportedExchange, publicKey, secretKey, userName)))
    }

    override fun createMetadataProvider(exchangeName: String): UserExchangeMetadataProvider {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        val exchangeSpec = ExchangeSpecification(supportedExchange.toXchangeClass().java)
        return DefaultUserExchangeMetadataProvider(exchangeName, metadataFromExchange(supportedExchange, getXchange(supportedExchange, exchangeSpec)))
    }

    override fun createWalletService(exchangeName: String, publicKey: String, secretKey: String, userName: String?): UserExchangeWalletService {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        return XchangeUserExchangeWalletService(supportedExchange, getXchange(supportedExchange, publicKey, secretKey, userName).accountService)
    }

    override fun createTickerService(exchangeName: String): UserExchangeTickerService {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        val exchangeSpec = ExchangeSpecification(supportedExchange.toXchangeClass().java)
        return XchangeUserExchangeTickerService(getXchange(supportedExchange, exchangeSpec).marketDataService)
    }

    override fun createTickerService(exchangeName: String, publicKey: String, secretKey: String, userName: String?): UserExchangeTickerService {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        return XchangeUserExchangeTickerService(getXchange(supportedExchange, publicKey, secretKey, userName).marketDataService)
    }

    override fun createTickerListenerRegistrar(exchangeName: String, publicKey: String, secretKey: String, userName: String?): TickerListenerRegistrar {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        return DefaultTickerListenerRegistrar(supportedExchange, createTickerService(exchangeName, publicKey, secretKey, userName))
    }

    override fun createTickerListenerRegistrar(exchangeName: String): TickerListenerRegistrar {
        val supportedExchange = SupportedExchange.fromExchangeName(exchangeName)
        return DefaultTickerListenerRegistrar(supportedExchange, createTickerService(exchangeName))
    }

    private fun getXchange(exchangeName: SupportedExchange, publicKey: String, secretKey: String, userName: String?): Exchange {
        val exchangeSpec = ExchangeSpecification(exchangeName.toXchangeClass().java)
        assignKeys(exchangeName, exchangeSpec, publicKey, secretKey, userName)
        return getXchange(exchangeName, exchangeSpec)
    }

    private fun setupMetadataInit(supportedExchange: SupportedExchange, exchangeSpec: ExchangeSpecification) {
        when (supportedExchange) {
            // >> providing fresh metadata working
            // run LoadingMetadataManualTest to see which exchanges are able to load fresh metadata
            BINANCE, BITMEX, CRYPTOPIA, KRAKEN, KUCOIN -> {
                exchangeSpec.metaDataJsonFileOverride = xchangeMetadataFile.getEmptyMetadataFile().absolutePath
            }
            BITTREX -> {
                exchangeSpec.isShouldLoadRemoteMetaData = false
                exchangeSpec.metaDataJsonFileOverride = xchangeMetadataFile.fetchBittrexMetadataFile().absolutePath
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
            logger.info("Creating $supportedExchange exchange for key '$keyTruncated' and userName '${exchangeSpec.userName}'")
            setupMetadataInit(supportedExchange, exchangeSpec)
            val xchange = xchangeFactory.createExchange(exchangeSpec)
            xchangesCache[cacheKey] = xchange
        }
        return xchangesCache[cacheKey]!!
    }

    private fun assignKeys(supportedExchange: SupportedExchange, exchangeSpecification: ExchangeSpecification, publicKey: String, secretKey: String, userName: String?) {
        exchangeSpecification.apiKey = publicKey.trim()
        exchangeSpecification.secretKey = secretKey.trim()
        exchangeSpecification.userName = userName

        if (exchangeSpecification.apiKey != publicKey) logger.warn("$supportedExchange API public key contained whitespaces, trimmed")
        if (exchangeSpecification.secretKey != secretKey) logger.warn("$supportedExchange API secret key contained whitespaces, trimmed")
        verifyKeys(supportedExchange, exchangeSpecification)
    }

    private fun verifyKeys(supportedExchange: SupportedExchange, exchangeSpecification: ExchangeSpecification) {
        if (exchangeSpecification.apiKey.isNullOrEmpty()) throw IllegalArgumentException("Exchange api key is not provided")
        if (exchangeSpecification.secretKey.isNullOrEmpty()) throw IllegalArgumentException("Exchange secret key is not provided")
        if (exchangeSpecification.apiKey == exchangeSpecification.secretKey) throw IllegalArgumentException("Secret key and api key cannot be the same")
        if (supportedExchange == BITSTAMP) {
            if (exchangeSpecification.userName.isNullOrEmpty()) throw IllegalArgumentException("User name for bitstamp is not provided")
        }
    }

}
