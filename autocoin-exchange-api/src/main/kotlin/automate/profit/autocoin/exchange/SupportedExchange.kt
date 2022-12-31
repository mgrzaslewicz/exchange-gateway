package automate.profit.autocoin.exchange

/**
 * After adding new exchange remember to update:
 * ExchangeMetadataFetcher
 * SupportedExchangeToXchangeClass
 */
enum class SupportedExchange(val exchangeName: String) {
    BIBOX("bibox"),
    BINANCE("binance"),
    BITBAY("bitbay"),
    BITMEX("bitmex"),
    BITSTAMP("bitstamp"),
    BITTREX("bittrex"),
    BITZ("bitz"), // requires key for reading ticker
    GATEIO("gateio"),
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
