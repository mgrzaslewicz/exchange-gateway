package automate.profit.autocoin.exchange

/**
 * After adding new exchange remember to update:
 * SupportedExchange.toXchangeClass
 */
enum class SupportedExchange(val exchangeName: String, val comments: List<String> = emptyList()) {
    BIBOX("bibox"),
    BINANCE("binance"),
    BITBAY("bitbay"),
    BITFINEX("bitfinex", comments = listOf("Needs update in xchange library to use v2 api")),
    BITMEX("bitmex"),
    BITSO("bitso", comments = listOf("Needs update in xchange library to use v3 api")),
    BITSTAMP("bitstamp"),
    BITTREX("bittrex"),
    BITZ("bitz", comments = listOf("Requires API key for reading ticker")),
    BLEUTRADE("bleutrade"),
    HITBTC("hitbtc"),
    CEXIO("cexio"),
    COINBASEPRO("coinbasepro"),
    COINBENE("coinbene", comments = listOf("throws 429 error, too frequent requests")),
    COINDEAL("coindeal", comments = listOf("CoindealMarketDataService.getTicker not implemented yet")),
    COINEX("coinex", comments = listOf("xchange market data service not implemented")),
    EXMO("exmo"),
    GATEIO("gateio", comments = listOf("Requires API key for currency metadata (not for currency pairs)")),
    GEMINI("gemini"),
    IDEX("idex", comments = listOf("xchange not adjusted to api changes")),
    KRAKEN("kraken"),
    KUCOIN("kucoin"),
    LUNO("luno"),
    LIVECOIN("livecoin"),
    POLONIEX("poloniex", listOf("Requires API key for reading ticker")),
    TRADEOGRE("tradeogre"),
    YOBIT("yobit");

    companion object {
        fun fromExchangeName(exchangeName: String): SupportedExchange =
                values().find { it.exchangeName.lowercase() == exchangeName }
                        ?: throw IllegalArgumentException("Unknown exchange name: $exchangeName")
    }
}
