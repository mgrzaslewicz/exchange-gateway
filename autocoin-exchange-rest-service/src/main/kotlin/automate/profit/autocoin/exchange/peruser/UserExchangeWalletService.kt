package automate.profit.autocoin.exchange.peruser

import automate.profit.autocoin.exchange.currency.CurrencyBalance
import automate.profit.autocoin.exchange.SupportedExchange
import mu.KLogging
import org.knowm.xchange.currency.Currency
import org.knowm.xchange.service.account.AccountService
import java.math.BigDecimal

interface UserExchangeWalletService {
    fun getAvailableAmountOfCurrency(currencyCode: String): BigDecimal = getCurrencyBalance(currencyCode).available
    fun getCurrencyBalance(currencyCode: String): CurrencyBalance
    fun getCurrencyBalances(): List<CurrencyBalance>
}

open class XchangeUserExchangeWalletService(private val supportedExchange: SupportedExchange, private val wrapped: AccountService) : UserExchangeWalletService {

    companion object : KLogging()

    override fun getCurrencyBalance(currencyCode: String): CurrencyBalance {
        logger.info("Requesting currency balance for $supportedExchange-$currencyCode")
        val accountInfo = wrapped.accountInfo

        val balance = accountInfo.wallet.getBalance(Currency(currencyCode))
        return CurrencyBalance(
                currencyCode = currencyCode,
                available = balance.available,
                frozen = balance.frozen,
                total = balance.total
        ).also {
            logger.info("Available $currencyCode balance: $it")
        }
    }

    override fun getCurrencyBalances(): List<CurrencyBalance> {
        logger.info("Requesting currency balances for $supportedExchange")
        val accountInfo = wrapped.accountInfo
        val balances = mutableListOf<CurrencyBalance>()
        accountInfo.wallets.forEach { _, wallet ->
            wallet.balances.forEach { currency, balance ->
                balances.add(CurrencyBalance(
                        currencyCode = currency.currencyCode,
                        available = balance.available,
                        frozen = balance.frozen,
                        total = balance.total
                ))
            }
        }
        logger.info("Requesting currency balances for $supportedExchange done")
        return balances
    }

}
