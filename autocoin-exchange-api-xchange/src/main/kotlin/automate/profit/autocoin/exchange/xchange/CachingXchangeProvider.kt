package automate.profit.autocoin.exchange.xchange

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import org.knowm.xchange.utils.DigestUtils
import java.security.MessageDigest
import java.util.function.Supplier
import org.knowm.xchange.Exchange as XchangeExchange

class CachingXchangeProvider(private val decorated: XchangeProvider) : XchangeProvider {
    private val cache = mutableMapOf<String, XchangeExchange>()

    private fun String?.md5(): String {
        return if (this == null) "null"
        else {
            val md = MessageDigest.getInstance("MD5")
            DigestUtils.bytesToHex(md.digest(toByteArray()))
        }
    }

    override fun invoke(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>?,
    ): XchangeExchange {
        val key = apiKey?.get()
        val cacheKey = if (key != null) {
            "$exchangeName:${key.secretKey.md5()}:${key.secretKey.md5()}"
        }
        else {
            exchangeName.value
        }
        return cache.getOrPut(cacheKey) {
            decorated(exchangeName, apiKey)
        }
    }
}
