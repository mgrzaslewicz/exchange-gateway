package automate.profit.autocoin.exchange.peruser

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyBalance
import mu.KLogging
import org.knowm.xchange.currency.Currency
import org.knowm.xchange.dto.account.Wallet
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
        val wallet = getTradingWallet()
        val balance = wallet.getBalance(Currency(currencyCode))
        return CurrencyBalance(
                currencyCode = currencyCode,
                available = balance?.available ?: BigDecimal.ZERO,
                frozen = balance?.frozen ?: BigDecimal.ZERO,
                total = balance?.total ?: BigDecimal.ZERO
        ).also {
            logger.info("Available $currencyCode balance: $it")
        }
    }

    private fun getTradingWallet(): Wallet {
        val accountInfo = wrapped.accountInfo
        val wallets = accountInfo.wallets
        val expectedWalletNameWhenMultipleExist = "trade"
        return if (wallets.size > 1) {
            if (!wallets.containsKey(expectedWalletNameWhenMultipleExist)) {
                logger.warn { "$supportedExchange has multiple wallets, but none named '$expectedWalletNameWhenMultipleExist'" }
            }
            wallets.getValue(expectedWalletNameWhenMultipleExist)
        } else {
            accountInfo.wallet
        }
    }

    override fun getCurrencyBalances(): List<CurrencyBalance> {
        logger.info("Requesting currency balances for $supportedExchange")
        val wallet = getTradingWallet()
        val balances = mutableListOf<CurrencyBalance>()
        wallet.balances.forEach { (currency, balance) ->
            balances.add(CurrencyBalance(
                    currencyCode = currency.currencyCode,
                    available = balance.available,
                    frozen = balance.frozen,
                    total = balance.total
            ))
        }
        logger.info("Requesting currency balances for $supportedExchange done")
        return balances
    }

}
