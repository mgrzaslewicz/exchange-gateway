package automate.profit.autocoin.exchange.wallet

import automate.profit.autocoin.exchange.apikey.ExchangeKeyService
import automate.profit.autocoin.exchange.apikey.ExchangeService
import automate.profit.autocoin.exchange.currency.CurrencyBalance
import automate.profit.autocoin.exchange.peruser.UserExchangeServicesFactory
import automate.profit.autocoin.exchange.peruser.UserExchangeWalletService
import java.math.BigDecimal

class XchangeExchangeWalletService(private val exchangeService: ExchangeService,
                                   private val exchangeKeyService: ExchangeKeyService,
                                   private val userExchangeServicesFactory: UserExchangeServicesFactory) : ExchangeWalletService {
    override fun getCurrencyBalance(exchangeName: String, exchangeUserId: String, currencyCode: String): CurrencyBalance {
        val userExchangeWalletService = getUserExchangeWalletService(exchangeName, exchangeUserId)
        return userExchangeWalletService.getCurrencyBalance(currencyCode)
    }

    private fun getUserExchangeWalletService(exchangeName: String, exchangeUserId: String): UserExchangeWalletService {
        val exchangeId = exchangeService.getExchangeIdByName(exchangeName)
        val exchangeKey = exchangeKeyService.getExchangeKey(exchangeUserId, exchangeId)
                ?: throw IllegalArgumentException("Exchange key for Exchange(name=$exchangeName,id=$exchangeId) and exchangeUserId=$exchangeUserId not found")
        return userExchangeServicesFactory.createWalletService(exchangeName, exchangeKey.apiKey, exchangeKey.secretKey, exchangeKey.userName)

    }

}
