package automate.profit.autocoin.exchange

/**
 * After adding new exchange remember to update:
 * SupportedExchangeToXchangeClass
 */
enum class SupportedExchange(val exchangeName: String) {
    BIBOX("bibox"),
    BINANCE("binance"),
    BITBAY("bitbay"),
    BITFINEX("bitfinex"), // needs update in xchange library to use v2 api
    BITMEX("bitmex"),
    BITSO("bitso"), // needs update in xchange library to use v3 api
    BITSTAMP("bitstamp"),
    BITTREX("bittrex"),
    BITZ("bitz"), // requires key for reading ticker
    COINBASEPRO("coinbasepro"),
    COINEX("coinex"), // xchange market data service not implemented
    GATEIO("gateio"),
    GEMINI("gemini"),
    IDEX("idex"), // xchange not adjusted to api changes
    KRAKEN("kraken"),
    KUCOIN("kucoin"),
    POLONIEX("poloniex"),
    TRADEOGRE("tradeogre"),
    YOBIT("yobit");

    companion object {
        fun fromExchangeName(exchangeName: String): SupportedExchange =
                values().find { it.exchangeName.toLowerCase() == exchangeName }
                        ?: throw IllegalArgumentException("Unknown exchange name: $exchangeName")
    }
}
