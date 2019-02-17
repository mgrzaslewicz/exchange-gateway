package automate.profit.autocoin.exchange.wallet

import automate.profit.autocoin.exchange.apikey.ExchangeKeyService
import automate.profit.autocoin.exchange.apikey.ExchangeService
import automate.profit.autocoin.exchange.peruser.UserExchangeServicesFactory
import automate.profit.autocoin.exchange.peruser.UserExchangeWalletService
import java.math.BigDecimal

interface ExchangeWalletService {
    fun getAvailableAmountOfCurrency(exchangeName: String, exchangeUserId: String, currencyCode: String): BigDecimal
}

class DefaultExchangeWalletService(private val exchangeService: ExchangeService,
                                private val exchangeKeyService: ExchangeKeyService,
                                private val userExchangeServicesFactory: UserExchangeServicesFactory) : ExchangeWalletService {
    override fun getAvailableAmountOfCurrency(exchangeName: String, exchangeUserId: String, currencyCode: String): BigDecimal {
        val userExchangeWalletService = getUserExchangeWalletService(exchangeName, exchangeUserId)
        return userExchangeWalletService.getAvailableAmountOfCurrency(currencyCode)
    }

    private fun getUserExchangeWalletService(exchangeName: String, exchangeUserId: String): UserExchangeWalletService {
        val exchangeId = exchangeService.getExchangeIdByName(exchangeName)
        val exchangeKey = exchangeKeyService.getExchangeKey(exchangeUserId, exchangeId)
                ?: throw IllegalArgumentException("Exchange key for Exchange(name=$exchangeName,id=$exchangeId) and exchangeUserId=$exchangeUserId not found")
        return userExchangeServicesFactory.createWalletService(exchangeName, exchangeKey.apiKey, exchangeKey.secretKey, exchangeKey.userName)

    }

}
