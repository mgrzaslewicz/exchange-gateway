package automate.profit.autocoin.spi.exchange.wallet.gateway

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.autocoin.spi.exchange.currency.CurrencyBalance
import automate.profit.autocoin.spi.exchange.wallet.service.WalletService
import java.util.function.Supplier

class DelegateWalletServiceGateway(
    private val walletServices: Map<ExchangeName, WalletService>,
) : WalletServiceGateway {
    override fun getCurrencyBalance(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
        currencyCode: String,
    ): CurrencyBalance {
        return walletServices.getValue(exchangeName).getCurrencyBalance(apiKey, currencyCode)
    }

    override fun getCurrencyBalances(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>,
    ): List<CurrencyBalance> {
        return walletServices.getValue(exchangeName).getCurrencyBalances(apiKey)
    }

}
