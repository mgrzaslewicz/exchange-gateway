package automate.profit.autocoin.exchange.wallet

import java.math.BigDecimal

interface ExchangeWalletService {
    fun getAvailableAmountOfCurrency(exchangeName: String, exchangeUserId: String, currencyCode: String): BigDecimal
}

