package automate.profit.autocoin.exchange.peruser

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyBalance
import automate.profit.autocoin.exchange.ratelimiter.ExchangeRateLimiter
import automate.profit.autocoin.exchange.ratelimiter.RateLimiterBehavior
import automate.profit.autocoin.exchange.ratelimiter.RateLimiterBehavior.WAIT_WITHOUT_TIMEOUT
import automate.profit.autocoin.exchange.ratelimiter.acquireWith
import mu.KLogging
import org.knowm.xchange.currency.Currency
import org.knowm.xchange.dto.account.Wallet
import java.math.BigDecimal
import org.knowm.xchange.service.account.AccountService as XchangeAccountService

interface UserExchangeWalletService {
    fun getAvailableAmountOfCurrency(currencyCode: String, rateLimiterBehavior: RateLimiterBehavior = WAIT_WITHOUT_TIMEOUT): BigDecimal =
        getCurrencyBalance(currencyCode, rateLimiterBehavior).available

    fun getCurrencyBalance(currencyCode: String, rateLimiterBehavior: RateLimiterBehavior = WAIT_WITHOUT_TIMEOUT): CurrencyBalance
    fun getCurrencyBalances(rateLimiterBehavior: RateLimiterBehavior = WAIT_WITHOUT_TIMEOUT): List<CurrencyBalance>
}

open class XchangeUserExchangeWalletService(
    private val supportedExchange: SupportedExchange,
    private val wrapped: XchangeAccountService,
    private val exchangeRateLimiter: ExchangeRateLimiter,
) : UserExchangeWalletService {

    companion object : KLogging()

    override fun getCurrencyBalance(currencyCode: String, rateLimiterBehaviour: RateLimiterBehavior): CurrencyBalance {
        logger.info("Requesting currency balance for $supportedExchange-$currencyCode")
        val wallet = getTradingWallet(rateLimiterBehaviour)
        val balance = wallet.getBalance(Currency(currencyCode))
        return CurrencyBalance(
            currencyCode = currencyCode,
            available = balance?.available ?: BigDecimal.ZERO,
            frozen = balance?.frozen ?: BigDecimal.ZERO,
            total = balance?.total ?: BigDecimal.ZERO
        ).also {
            logger.info("[$supportedExchange] Available $currencyCode balance: $it")
        }
    }

    private fun getTradingWallet(rateLimiterBehaviour: RateLimiterBehavior): Wallet {
        exchangeRateLimiter.acquireWith(rateLimiterBehaviour) { "[$supportedExchange] Could not acquire permit to getTradingWallet" }
        val accountInfo = wrapped.accountInfo
        val wallets = accountInfo.wallets
        val expectedWalletNameWhenMultipleExist = "trade"
        return if (wallets.size > 1) {
            if (!wallets.containsKey(expectedWalletNameWhenMultipleExist)) {
                logger.warn { "[$supportedExchange] There are multiple wallets, but none named '$expectedWalletNameWhenMultipleExist'" }
            }
            wallets.getValue(expectedWalletNameWhenMultipleExist)
        } else {
            accountInfo.wallet
        }
    }

    override fun getCurrencyBalances(rateLimiterBehaviour: RateLimiterBehavior): List<CurrencyBalance> {
        logger.info("[$supportedExchange] Requesting currency balances")
        val wallet = getTradingWallet(rateLimiterBehaviour)
        val balances = mutableListOf<CurrencyBalance>()
        wallet.balances.forEach { (currency, balance) ->
            balances.add(
                CurrencyBalance(
                    currencyCode = currency.currencyCode,
                    available = balance.available,
                    frozen = balance.frozen,
                    total = balance.total
                )
            )
        }
        logger.info("[$supportedExchange] Requesting currency balances done")
        return balances
    }

}
