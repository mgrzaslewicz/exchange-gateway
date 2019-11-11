package automate.profit.autocoin.exchange

enum class SupportedExchange(val exchangeName: String) {
    BINANCE("binance"),
    BITBAY("bitbay"),
    BITMEX("bitmex"),
    BITSTAMP("bitstamp"),
    BITTREX("bittrex"),
    //CRYPTOPIA("cryptopia"), // exchange does not exist anymore
    GATEIO("gateio"),
    KRAKEN("kraken"),
    KUCOIN("kucoin"),
    POLONIEX("poloniex"),
    YOBIT("yobit");

    companion object {
        fun fromExchangeName(exchangeName: String): SupportedExchange =
                values().find { it.exchangeName.toLowerCase() == exchangeName }
                        ?: throw IllegalArgumentException("Unknown exchange name: $exchangeName")
    }
}
