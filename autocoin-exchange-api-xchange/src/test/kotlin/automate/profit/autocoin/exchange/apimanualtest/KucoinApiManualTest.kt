package automate.profit.autocoin.exchange.apimanualtest

import automate.profit.autocoin.api.exchange.ApiKey
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.kucoin
import automate.profit.autocoin.exchange.xchange.XchangeApiKeyVerifierGateway
import automate.profit.autocoin.exchange.xchange.XchangeSpecificationApiKeyAssigner
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.kucoin.KucoinExchange
import java.lang.System.getenv

fun main() {
    val exchangeSpecification = ExchangeSpecification(KucoinExchange::class.java)
    val apiKey = ApiKey(
        publicKey = getenv("PUBLIC_KEY"),
        secretKey = getenv("SECRET_KEY"),
        exchangeSpecificKeyParameters = mapOf("passphrase" to getenv("PASSPHRASE")),
    )
    XchangeSpecificationApiKeyAssigner(apiKeyVerifierGateway = XchangeApiKeyVerifierGateway())
        .assignKeys(exchangeName = kucoin, exchangeSpecification = exchangeSpecification, apiKeySupplier = { apiKey })
    val kucoinExchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpecification) as KucoinExchange
    val accounts = kucoinExchange.accountService.kucoinAccounts
//    val tradeFees = kucoinExchange.marketDataService.getKucoinTradeFee("1EARTH-USDT") // expected to be OK
    val tradeFees = kucoinExchange.marketDataService.getKucoinTradeFee("1EARTH-USDT") // expected to be OK
//    val tradeFees = kucoinExchange.marketDataService.getKucoinTradeFee("1EARTH-USDT,1INCH-USDT") // surprise: 401 for more than 2 symbols
    println("done")
}
