package automate.profit.autocoin.exchange

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

enum class SupportedExchange(val exchangeName: String, val exchangeClass: KClass<out Exchange>) {
    BINANCE("binance", BinanceExchange::class),
    BITBAY("bitbay", BitbayExchange::class),
    BITMEX("bitmex", BitmexExchange::class),
    BITSTAMP("bitstamp", BitstampExchange::class),
    BITTREX("bittrex", BittrexExchange::class),
    CRYPTOPIA("cryptopia", CryptopiaExchange::class),
    GATEIO("gateio", GateioExchange::class),
    KRAKEN("kraken", KrakenExchange::class),
    KUCOIN("kucoin", KucoinExchange::class),
    POLONIEX("poloniex", PoloniexExchange::class),
    YOBIT("yobit", YoBitExchange::class);

    companion object {
        fun fromExchangeName(exchangeName: String): SupportedExchange =
                values().find { it.exchangeName == exchangeName }
                        ?: throw IllegalArgumentException("Unknown exchange name: $exchangeName")
    }
}
