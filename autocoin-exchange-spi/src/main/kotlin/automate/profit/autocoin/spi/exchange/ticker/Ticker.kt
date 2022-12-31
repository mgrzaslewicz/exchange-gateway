package automate.profit.autocoin.spi.exchange.ticker

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import java.math.BigDecimal

interface Ticker {
    val exchangeName: ExchangeName
    val currencyPair: CurrencyPair
    val ask: BigDecimal
    val bid: BigDecimal
    val baseCurrency24hVolume: BigDecimal
    val counterCurrency24hVolume: BigDecimal
    val receivedAtMillis: Long
    val exchangeTimestampMillis: Long?
    fun hasTimestamp() = this.exchangeTimestampMillis != null
}
