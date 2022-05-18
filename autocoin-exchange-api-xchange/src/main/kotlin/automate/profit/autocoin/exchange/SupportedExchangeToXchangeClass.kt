package automate.profit.autocoin.exchange

import automate.profit.autocoin.exchange.SupportedExchange.*
import automate.profit.xchange.HitBtcExchangeFork
import automate.profit.xchange.PoloniexExchangeFork
import automate.profit.xchange.ZondaBitbayExchangFork
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
import org.knowm.xchange.bleutrade.BleutradeExchange
import org.knowm.xchange.cexio.CexIOExchange
import org.knowm.xchange.coinbasepro.CoinbaseProExchange
import org.knowm.xchange.coinbene.CoinbeneExchange
import org.knowm.xchange.coindeal.CoindealExchange
import org.knowm.xchange.coinex.CoinexExchange
import org.knowm.xchange.exmo.ExmoExchange
import org.knowm.xchange.gateio.GateioExchange
import org.knowm.xchange.gemini.v1.GeminiExchange
import org.knowm.xchange.hitbtc.v2.HitbtcExchange
import org.knowm.xchange.idex.IdexExchange
import org.knowm.xchange.kraken.KrakenExchange
import org.knowm.xchange.kucoin.KucoinExchange
import org.knowm.xchange.livecoin.LivecoinExchange
import org.knowm.xchange.luno.LunoExchange
import org.knowm.xchange.poloniex.PoloniexExchange
import org.knowm.xchange.tradeogre.TradeOgreExchange
import org.knowm.xchange.yobit.YoBitExchange

fun SupportedExchange.toXchangeJavaClass(): Class<out Exchange> {
    return when (this) {
        BIBOX -> BiboxExchange::class.java
        BINANCE -> BinanceExchange::class.java
        BITBAY -> ZondaBitbayExchangFork::class.java
        BITFINEX -> BitfinexExchange::class.java
        BITMEX -> BitmexExchange::class.java
        BITSO -> BitsoExchange::class.java
        BITSTAMP -> BitstampExchange::class.java
        BITTREX -> BittrexExchange::class.java
        BLEUTRADE -> BleutradeExchange::class.java
        CEXIO -> CexIOExchange::class.java
        EXMO -> ExmoExchange::class.java
        COINBASEPRO -> CoinbaseProExchange::class.java
        COINBENE -> CoinbeneExchange::class.java
        COINDEAL -> CoindealExchange::class.java
        COINEX -> CoinexExchange::class.java
        GATEIO -> GateioExchange::class.java
        HITBTC -> HitBtcExchangeFork::class.java
        GEMINI -> GeminiExchange::class.java
        IDEX -> IdexExchange::class.java
        KRAKEN -> KrakenExchange::class.java
        KUCOIN -> KucoinExchange::class.java
        LUNO -> LunoExchange::class.java
        LIVECOIN -> LivecoinExchange::class.java
        POLONIEX -> PoloniexExchangeFork::class.java
        TRADEOGRE -> TradeOgreExchange::class.java
        YOBIT -> YoBitExchange::class.java
        else -> throw IllegalArgumentException("Unknown exchange name: ${this.exchangeName}")
    }
}
