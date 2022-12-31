package automate.profit.autocoin.exchange

import automate.profit.autocoin.exchange.SupportedExchange.*
import org.knowm.xchange.Exchange
import org.knowm.xchange.bibox.BiboxExchange
import org.knowm.xchange.binance.BinanceExchange
import org.knowm.xchange.bitbay.BitbayExchange
import org.knowm.xchange.bitfinex.BitfinexExchange
import org.knowm.xchange.bitmex.BitmexExchange
import org.knowm.xchange.bitso.BitsoExchange
import org.knowm.xchange.bitstamp.BitstampExchange
import org.knowm.xchange.bittrex.BittrexExchange
import org.knowm.xchange.bitz.BitZExchange
import org.knowm.xchange.coinbasepro.CoinbaseProExchange
import org.knowm.xchange.coinex.CoinexExchange
import org.knowm.xchange.gateio.GateioExchange
import org.knowm.xchange.gemini.v1.GeminiExchange
import org.knowm.xchange.idex.IdexExchange
import org.knowm.xchange.kraken.KrakenExchange
import org.knowm.xchange.kucoin.KucoinExchange
import org.knowm.xchange.poloniex.PoloniexExchange
import org.knowm.xchange.tradeogre.TradeOgreExchange
import org.knowm.xchange.yobit.YoBitExchange
import kotlin.reflect.KClass

fun SupportedExchange.toXchangeClass(): KClass<out Exchange> {
    return when (this) {
        BIBOX -> BiboxExchange::class
        BINANCE -> BinanceExchange::class
        BITBAY -> BitbayExchange::class
        BITFINEX -> BitfinexExchange::class
        BITMEX -> BitmexExchange::class
        BITSO -> BitsoExchange::class
        BITSTAMP -> BitstampExchange::class
        BITTREX -> BittrexExchange::class
        BITZ -> BitZExchange::class
        COINBASEPRO -> CoinbaseProExchange::class
        COINEX -> CoinexExchange::class
        GATEIO -> GateioExchange::class
        GEMINI -> GeminiExchange::class
        IDEX -> IdexExchange::class
        KRAKEN -> KrakenExchange::class
        KUCOIN -> KucoinExchange::class
        POLONIEX -> PoloniexExchange::class
        TRADEOGRE -> TradeOgreExchange::class
        YOBIT -> YoBitExchange::class
        else -> throw IllegalArgumentException("Unknown exchange name: ${this.exchangeName}")
    }
}
