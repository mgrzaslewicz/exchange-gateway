package automate.profit.autocoin.spi.exchange


interface Exchange {
    val exchangeName: ExchangeName
    val comments: List<String>
}
