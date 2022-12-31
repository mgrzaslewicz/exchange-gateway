package com.autocoin.exchangegateway.api.exchange.xchange

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import org.knowm.xchange.utils.DigestUtils
import java.security.MessageDigest
import org.knowm.xchange.Exchange as XchangeExchange

class CachingXchangeProvider<T>(private val decorated: XchangeProvider<T>) : XchangeProvider<T> {
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
        apiKey: ApiKeySupplier<T>,
    ): XchangeExchange {
        val key = apiKey.supplier?.get()
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
