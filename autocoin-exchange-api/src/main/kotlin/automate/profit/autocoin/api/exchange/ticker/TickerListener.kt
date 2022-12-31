package automate.profit.autocoin.api.exchange.ticker

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import java.math.BigDecimal
import automate.profit.autocoin.spi.exchange.ticker.Ticker as SpiTicker

data class Ticker(
    override val exchangeName: ExchangeName,
    override val currencyPair: CurrencyPair,
    override val ask: BigDecimal,
    override val bid: BigDecimal,
    override val baseCurrency24hVolume: BigDecimal,
    override val counterCurrency24hVolume: BigDecimal,
    override val receivedAtMillis: Long,
    override val exchangeTimestampMillis: Long?,
): SpiTicker
