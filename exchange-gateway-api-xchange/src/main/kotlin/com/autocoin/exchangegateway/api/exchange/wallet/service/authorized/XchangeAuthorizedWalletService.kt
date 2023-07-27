package com.autocoin.exchangegateway.api.exchange.wallet.service.authorized

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyBalance
import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.wallet.WithdrawResult
import com.autocoin.exchangegateway.spi.exchange.wallet.service.authorized.AuthorizedWalletService
import org.knowm.xchange.currency.Currency
import org.knowm.xchange.dto.account.Wallet
import org.knowm.xchange.service.account.AccountService
import java.math.BigDecimal
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyBalance as SpiCurrencyBalance

class XchangeAuthorizedWalletService<T>(
    override val exchange: Exchange,
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
        } else {
            accountInfo.wallet
        }
    }

    override fun withdraw(
        currencyCode: String,
        amount: BigDecimal,
        address: String,
    ): WithdrawResult {
        val transactionId = delegate.withdrawFunds(Currency(currencyCode), amount, address)
        return object : WithdrawResult {
            override val transactionId = transactionId
        }
    }
}
