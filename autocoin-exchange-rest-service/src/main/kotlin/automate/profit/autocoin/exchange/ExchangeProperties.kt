package automate.profit.autocoin.exchange

class ExchangeProperties {

    var active: List<String> = emptyList()

    fun getActiveExchangeList(): List<SupportedExchange> = active.map { SupportedExchange.fromExchangeName(it) }.sortedBy { it.exchangeName }

}
