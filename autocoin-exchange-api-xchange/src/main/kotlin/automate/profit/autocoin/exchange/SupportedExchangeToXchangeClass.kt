package automate.profit.autocoin.exchange

import automate.profit.autocoin.exchange.SupportedExchange.*
import org.knowm.xchange.Exchange
import org.knowm.xchange.binance.BinanceExchange
import org.knowm.xchange.bitbay.BitbayExchange
import org.knowm.xchange.bitmex.BitmexExchange
import org.knowm.xchange.bitstamp.BitstampExchange
import org.knowm.xchange.bittrex.BittrexExchange
import org.knowm.xchange.cryptopia.CryptopiaExchange
import org.knowm.xchange.gateio.GateioExchange
import org.knowm.xchange.kraken.KrakenExchange
import org.knowm.xchange.kucoin.KucoinExchange
import org.knowm.xchange.poloniex.PoloniexExchange
import org.knowm.xchange.yobit.YoBitExchange
import kotlin.reflect.KClass

fun SupportedExchange.toXchangeClass(): KClass<out Exchange> {
    return when (this) {
        BINANCE -> BinanceExchange::class
        BITBAY -> BitbayExchange::class
        BITMEX -> BitmexExchange::class
        BITSTAMP -> BitstampExchange::class
        BITTREX -> BittrexExchange::class
        CRYPTOPIA -> CryptopiaExchange::class
        GATEIO -> GateioExchange::class
        KRAKEN -> KrakenExchange::class
        KUCOIN -> KucoinExchange::class
        POLONIEX -> PoloniexExchange::class
        YOBIT -> YoBitExchange::class
        else -> throw IllegalArgumentException("Unknown exchange name: ${this.exchangeName}")
    }
}
