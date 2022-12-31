package automate.profit.autocoin.exchange.wallet.service.authorized

import automate.profit.autocoin.api.exchange.currency.CurrencyBalance
import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier
import automate.profit.autocoin.spi.exchange.wallet.service.authorized.AuthorizedWalletService
import org.knowm.xchange.currency.Currency
import org.knowm.xchange.dto.account.Wallet
import org.knowm.xchange.service.account.AccountService
import java.math.BigDecimal
import automate.profit.autocoin.spi.exchange.currency.CurrencyBalance as SpiCurrencyBalance

class XchangeAuthorizedWalletService<T>(
    override val exchangeName: ExchangeName,
    override val apiKey: ApiKeySupplier<T>,
    val delegate: AccountService,
    private val expectedTradingWalletNameWhenMultipleExist: String,
) : AuthorizedWalletService<T> {

    override fun getCurrencyBalance(currencyCode: String): SpiCurrencyBalance {
        val wallet = getTradingWallet()
        val balance = wallet.getBalance(Currency(currencyCode))
        return CurrencyBalance(
            currencyCode = currencyCode,
            amountAvailable = balance?.available ?: BigDecimal.ZERO,
            amountInOrders = balance?.frozen ?: BigDecimal.ZERO,
            totalAmount = balance?.total ?: BigDecimal.ZERO,
        )
    }

    override fun getCurrencyBalances(): List<SpiCurrencyBalance> {
        val wallet = getTradingWallet()
        val balances = mutableListOf<SpiCurrencyBalance>()
        wallet.balances.forEach { (currency, balance) ->
            balances.add(
                CurrencyBalance(
                    currencyCode = currency.currencyCode,
                    amountAvailable = balance.available,
                    amountInOrders = balance.frozen,
                    totalAmount = balance.total,
                ),
            )
        }
        return balances
    }

    private fun getTradingWallet(): Wallet {
        val accountInfo = delegate.accountInfo
        val wallets = accountInfo.wallets
        return if (wallets.size > 1) {
            wallets.getValue(expectedTradingWalletNameWhenMultipleExist)
        }
        else {
            accountInfo.wallet
        }
    }
}
