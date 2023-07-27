package com.autocoin.exchangegateway.api.exchange.xchange

import com.autocoin.exchangegateway.api.exchange.xchange.fork.HitBtcExchangeFork
import com.autocoin.exchangegateway.spi.exchange.Exchange

interface XchangeExchange : Exchange {
    val xchangeClass: Class<out org.knowm.xchange.Exchange>
}

enum class SupportedXchangeExchange(
    override val exchangeName: String,
    override val xchangeClass: Class<out org.knowm.xchange.Exchange>,
) : XchangeExchange {
    bibox("bibox", org.knowm.xchange.bibox.BiboxExchange::class.java),
    binance("binance", org.knowm.xchange.binance.BinanceExchange::class.java),
    bitfinex("bitfinex", org.knowm.xchange.bitfinex.BitfinexExchange::class.java),
    bitmex("bitmex", org.knowm.xchange.bitmex.BitmexExchange::class.java),
    bitso("bitso", org.knowm.xchange.bitso.BitsoExchange::class.java),
    bitstamp("bitstamp", org.knowm.xchange.bitstamp.BitstampExchange::class.java),
    bittrex("bittrex", org.knowm.xchange.bittrex.BittrexExchange::class.java),
    cexio("cexio", org.knowm.xchange.cexio.CexIOExchange::class.java),
    coinbasepro("coinbasepro", org.knowm.xchange.coinbasepro.CoinbaseProExchange::class.java),
    coindeal("coindeal", org.knowm.xchange.coindeal.CoindealExchange::class.java),
    coinex("coinex", org.knowm.xchange.coinex.CoinexExchange::class.java),
    exmo("exmo", org.knowm.xchange.exmo.ExmoExchange::class.java),
    ftx("ftx", org.knowm.xchange.ftx.FtxExchange::class.java),
    hitbtc("hitbtc", HitBtcExchangeFork::class.java),
    gateio("gateio", org.knowm.xchange.gateio.GateioExchange::class.java),
    gemini("gemini", org.knowm.xchange.gemini.v1.GeminiExchange::class.java),
    idex("idex", org.knowm.xchange.idex.IdexExchange::class.java),
    kraken("kraken", org.knowm.xchange.kraken.KrakenExchange::class.java),
    kucoin("kucoin", org.knowm.xchange.kucoin.KucoinExchange::class.java),
    luno("luno", org.knowm.xchange.luno.LunoExchange::class.java),
    okex("okex", org.knowm.xchange.okex.OkexExchange::class.java),
    poloniex("poloniex", org.knowm.xchange.poloniex.PoloniexExchange::class.java),
    tradeogre("tradeogre", org.knowm.xchange.tradeogre.TradeOgreExchange::class.java),
    yobit("yobit", org.knowm.xchange.yobit.YoBitExchange::class.java),
    zhonda("zhonda", org.knowm.xchange.bitbay.BitbayExchange::class.java);
}
