package automate.profit.autocoin.exchange.wallet

import automate.profit.autocoin.exchange.apikey.ExchangeKeyDto
import automate.profit.autocoin.exchange.apikey.ExchangeKeyService
import automate.profit.autocoin.exchange.apikey.ExchangeService
import automate.profit.autocoin.exchange.currency.CurrencyBalance
import automate.profit.autocoin.exchange.peruser.UserExchangeServicesFactory
import automate.profit.autocoin.exchange.peruser.UserExchangeWalletService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KLogging

class XchangeExchangeWalletService(private val exchangeService: ExchangeService,
                                   private val exchangeKeyService: ExchangeKeyService,
                                   private val userExchangeServicesFactory: UserExchangeServicesFactory) : ExchangeWalletService {

    companion object : KLogging()

    fun getCurrencyBalances(exchangeName: String, exchangeKey: ExchangeKeyDto): List<CurrencyBalance> {
        val userExchangeWalletService = getUserExchangeWalletService(exchangeName, exchangeKey)
        return userExchangeWalletService.getCurrencyBalances()
    }

    override fun getCurrencyBalances(exchangeName: String, exchangeUserId: String): List<CurrencyBalance> {
        val userExchangeWalletService = getUserExchangeWalletService(exchangeName, exchangeUserId)
        return userExchangeWalletService.getCurrencyBalances()
    }


    override fun getCurrencyBalance(exchangeName: String, exchangeUserId: String, currencyCode: String): CurrencyBalance {
        val userExchangeWalletService = getUserExchangeWalletService(exchangeName, exchangeUserId)
        return userExchangeWalletService.getCurrencyBalance(currencyCode)
    }

    fun getCurrencyBalance(exchangeName: String, exchangeKey: ExchangeKeyDto, currencyCode: String): CurrencyBalance {
        val userExchangeWalletService = getUserExchangeWalletService(exchangeName, exchangeKey)
        return userExchangeWalletService.getCurrencyBalance(currencyCode)
    }

    private fun getUserExchangeWalletService(exchangeName: String, exchangeKey: ExchangeKeyDto): UserExchangeWalletService {
        return userExchangeServicesFactory.createWalletService(exchangeName, exchangeKey.apiKey, exchangeKey.secretKey, exchangeKey.userName, exchangeKey.exchangeSpecificKeyParameters)

    }

    private fun getUserExchangeWalletService(exchangeName: String, exchangeUserId: String): UserExchangeWalletService {
        val exchangeId = exchangeService.getExchangeIdByName(exchangeName)
        val exchangeKey = exchangeKeyService.getExchangeKey(exchangeUserId, exchangeId)
                ?: throw IllegalArgumentException("Exchange key for Exchange(name=$exchangeName,id=$exchangeId) and exchangeUserId=$exchangeUserId not found")
        return userExchangeServicesFactory.createWalletService(exchangeName, exchangeKey.apiKey, exchangeKey.secretKey, exchangeKey.userName, exchangeKey.exchangeSpecificKeyParameters)

    }

    fun getCurrencyBalancesForEveryExchange(exchangeKeysGroupedByExchange: Map<String, List<ExchangeKeyDto>>): Map<ExchangeWithErrorMessage, List<CurrencyBalance>> {
        val result = mutableMapOf<ExchangeWithErrorMessage, List<CurrencyBalance>>()
        runBlocking {
            exchangeKeysGroupedByExchange.forEach {
                val exchangeName = exchangeService.getExchangeNameById(it.key)
                launch(Dispatchers.IO) {
                    try {
                        result[ExchangeWithErrorMessage(exchangeName, null)] = getAccountBalancesFor(exchangeName, it.value)
                    } catch (e: Exception) {
                        logger.error("Could not get response from $exchangeName exchange: $e")
                        result[ExchangeWithErrorMessage(exchangeName, e.message)] = emptyList()
                    }
                }
            }
        }
        return result
    }

    override fun getCurrencyBalancesForEveryExchange(exchangeUserId: String): Map<ExchangeWithErrorMessage, List<CurrencyBalance>> {
        val exchangeKeysGroupedByExchange = exchangeKeyService.getExchangeKeys(exchangeUserId).groupBy { it.exchangeId }
        return getCurrencyBalancesForEveryExchange(exchangeKeysGroupedByExchange)
    }

    private fun getAccountBalancesFor(exchangeName: String, exchangeKeys: List<ExchangeKeyDto>): List<CurrencyBalance> {
        return exchangeKeys.flatMap { exchangeKey ->
            val accountService = userExchangeServicesFactory.createWalletService(exchangeName, exchangeKey.apiKey, exchangeKey.secretKey, exchangeKey.userName, exchangeKey.exchangeSpecificKeyParameters)
            accountService.getCurrencyBalances()
        }
    }
}
