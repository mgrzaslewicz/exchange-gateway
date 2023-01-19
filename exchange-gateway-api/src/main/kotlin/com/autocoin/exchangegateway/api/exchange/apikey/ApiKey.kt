package com.autocoin.exchangegateway.api.exchange.apikey

import java.security.MessageDigest
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKey as SpiApiKey

private fun String.md5() = MessageDigest.getInstance("MD5").digest(this.toByteArray()).joinToString("") { "%02x".format(it) }

data class ApiKey(
    override val publicKey: String,
    override val secretKey: String,
    override val userName: String? = null,
    override val exchangeSpecificKeyParameters: Map<String, String>? = null,
) : SpiApiKey {
    init {
        check(publicKey.isNotBlank()) { "publicKey cannot be blank" }
        check(secretKey.isNotBlank()) { "secretKey cannot be blank" }
    }

    private val asString by lazy {
        "ApiKey(publicKey.md5()='${publicKey.md5()}', secretKey.md5()='${secretKey.md5()}', userName=${userName.hashCode()}"
    }

    override fun toString() = asString
}

